package es.archetyp.archetypes2.backend.archetype.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import es.archetyp.archetypes2.backend.archetype.boundary.ArchetypesDatabaseUpdatedEvent;
import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.backend.archetype.entity.Archetypes;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
class ArchetypesImporter {

	@Autowired
	private MavenConfig mavenConfig;

	@Autowired
	private PomParser pomParser;

	@Autowired
	private Archetypes archetypes;

	@Autowired
	private ApplicationEventPublisher publisher;

	private File rootDir;

	@PostConstruct
	public void init() {
		rootDir = new File(mavenConfig.getMavenRootDir());
		if (!rootDir.exists()) {
			throw new RuntimeException("Root dir does not exist.");
		}
		if (!rootDir.isDirectory()) {
			throw new RuntimeException("Root dir is not a directory.");
		}
	}

	@Scheduled(fixedRate = (24 * 60 * 60 + 1) * 1000)
	public void importNewArchetypes() {
		try {
			final Set<Archetype> allArchetypes = loadFromAllCatalogs();
			final Set<Archetype> newArchetypes = allArchetypes.parallelStream()
				.filter(a -> !archetypes
					.findByGroupIdAndArtifactIdAndVersion(a.getGroupId(), a.getArtifactId(), a.getVersion())
					.isPresent())
				.collect(Collectors.toSet());
			LOG.debug("Importing " + newArchetypes.size() + " new Archetypes.");
			final AtomicInteger current = new AtomicInteger(1);
			new ForkJoinPool(4).submit(() -> newArchetypes.stream().parallel().forEach(a -> {
				try {
					LOG.debug("Importing " + current.getAndIncrement() + "/" + newArchetypes.size());
					loadArchetypeContent(a);
					archetypes.save(a);
				} catch (final Exception e) {
					LOG.error("Could not import archetype " + a, e);
				}
			})).get();
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			LOG.debug("Import job finished.");
			publisher.publishEvent(new ArchetypesDatabaseUpdatedEvent());
		}
	}

	private void loadArchetypeContent(final Archetype archetype) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException {
		if (archetype.getLocalDir().isPresent()) {
			throw new ArchetypeAlreadyImportedException(archetype);
		}
		final File baseDir = buildArchetypeBaseDir(archetype);
		final ArchetypeGenerateResult generateResult = archetypeGenerate(archetype, "com.example", "example-app", baseDir);
		if (generateResult.isSuccess()) {
			final Pom pom = pomParser.parse(baseDir, archetype);
			archetype.setJavaVersion(pom.getJavaVersion());
			archetype.setPackaging(pom.getPackaging());
			archetype.setLastUpdated(pom.getLastUpdated());
			archetype.setLocalDir(Optional.of(baseDir.getAbsolutePath()));
		}
		archetype.setGenerateLog(Optional.of(generateResult.getOutput()));
	}

	private ArchetypeGenerateResult archetypeGenerate(final Archetype archetype, final String groupId, final String artifactId, final File baseDir) throws IOException, InvalidExitValueException, InterruptedException, TimeoutException {
		if (baseDir.exists()) {
			LOG.debug("Deleting already existing base directory: " + baseDir);
			FileUtils.deleteDirectory(baseDir);
		}
		if (!baseDir.mkdirs()) {
			throw new ArchetypeImportException("Cannot create base directory: " + baseDir);
		}
		final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
		List<String> additionalProps = new ArrayList<>();
		int exitValue = runProcess(archetype, groupId, artifactId, baseDir, stdOut, additionalProps);
		String log = new String(stdOut.toByteArray(), StandardCharsets.UTF_8);
		if (0 != exitValue) {
			additionalProps = extractAddiationalProps(log);
			if (!additionalProps.isEmpty()) {
				stdOut.reset();
				LOG.debug("Retrying with additional props: " + additionalProps);
				exitValue = runProcess(archetype, groupId, artifactId, baseDir, stdOut, additionalProps);
				if (0 == exitValue) {
					log = new String(stdOut.toByteArray(), StandardCharsets.UTF_8);
				}
			}
		}
		return new ArchetypeGenerateResult(exitValue, log.replace("\\", "/"), additionalProps);
	}

	private int runProcess(final Archetype archetype, final String groupId, final String artifactId, final File baseDir, final ByteArrayOutputStream stdOut, final List<String> additionalProps) throws IOException, InterruptedException, TimeoutException {
		final List<String> cmd = makeCommand(archetype, groupId, artifactId, additionalProps);
		return new ProcessExecutor(cmd)
			.redirectError(stdOut)
			.redirectOutput(stdOut)
			.destroyOnExit()
			.directory(baseDir)
			.timeout(10, TimeUnit.MINUTES)
			.execute()
			.getExitValue();
	}

	private List<String> makeCommand(final Archetype archetype, final String groupId, final String artifactId, final List<String> additionalProps) {
		final List<String> propsList = additionalProps
			.stream()
			.map(p -> "-D" + p + "=Example" + Arrays.asList(p.split("-"))
			.stream()
			.map(s -> s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1)))
			.collect(Collectors.toList());
		final List<String> cmd = new ArrayList<>(Arrays.asList(
			mavenConfig.getMavenCommand(),
			"archetype:generate",
			"-DinteractiveMode=false",
			"-DarchetypeGroupId=" + archetype.getGroupId(),
			"-DarchetypeArtifactId=" + archetype.getArtifactId(),
			"-DarchetypeVersion=" + archetype.getVersion(),
			"-DgroupId=" + groupId,
			"-DartifactId=" + artifactId,
			"-DprojectName=ExampleProject"));
		cmd.addAll(propsList);
		return cmd;
	}

	private List<String> extractAddiationalProps(final String log) {
		final Pattern pattern = Pattern.compile("(\\[ERROR\\] Property )(.*)( is missing.)");
		final Matcher matcher = pattern.matcher(log);
		final List<String> additionalProps = new ArrayList<>();
		while (matcher.find()) {
			final String string = matcher.group(2);
			if (!string.contains(",") && !string.contains(" ")) {
				additionalProps.add(string);
			}
		}
		return additionalProps;
	}

	private File buildArchetypeBaseDir(final Archetype archetype) {
		return new File(rootDir, String.join(File.separator,
			Arrays.asList(
				archetype.getGroupId().replace(".", File.separator),
				archetype.getArtifactId(),
				archetype.getVersion()
			)
		));
	}

	private Set<Archetype> loadFromAllCatalogs() throws DocumentException {
		final Set<Archetype> loadedArchetypes = new HashSet<>();
		final AtomicInteger countTotal = new AtomicInteger();
		for (final String repoUrl : mavenConfig.getMavenRepos()) {
			loadAllArchetypes(loadedArchetypes, countTotal, repoUrl);
		}
		LOG.debug("Total archetypes: " + countTotal.get());
		return loadedArchetypes;
	}

	private void loadAllArchetypes(final Set<Archetype> loadedArchetypes, final AtomicInteger countTotal, final String repoUrl) throws DocumentException {
		final SAXReader reader = new SAXReader();
		LOG.debug("Loading catalog from " + repoUrl + "...");
		final Document document = reader.read(repoUrl);
		LOG.debug("... loading catalog done.");
		LOG.debug("Selecting archetype nodes...");
		@SuppressWarnings("unchecked")
		final List<Node> archetypeNodes = document.selectNodes("//archetype");
		LOG.debug("... archetype node select done.");
		archetypeNodes
			.parallelStream()
			.forEach(n -> {
				loadedArchetypes.add(readArchetype(n, repoUrl));
				countTotal.incrementAndGet();
			});
	}

	private Archetype readArchetype(final Node archetypeNode, final String repositoryUrl) {
		final String groupId = archetypeNode.selectSingleNode("groupId").getText();
		final String artifactId = archetypeNode.selectSingleNode("artifactId").getText();
		final String version = archetypeNode.selectSingleNode("version").getText();
		final Node descriptionNode = archetypeNode.selectSingleNode("description");
		final Optional<String> description = descriptionNode != null ? Optional.of(descriptionNode.getText()) : Optional.empty();
		return new Archetype(groupId, artifactId, version, description, repositoryUrl);
	}

}
