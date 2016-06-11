package es.archetyp.archetypes2.backend.archetype.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
class PomParser {

	public Pom parse(final File baseDir, final Archetype archetype) {
		final File pomFile = new File(new File(baseDir, "example-app"), "pom.xml");
		try {
			return new PomImpl(pomFile, archetype);
		} catch (ParserConfigurationException | SAXException | IOException | DocumentException e) {
			LOG.error("Could not pom.xml", e);
			return new EmptyPomImpl();
		}
	}

	private static class PomImpl implements Pom {

		private final Document document;
		private final Archetype archetype;

		public PomImpl(final File pomFile, final Archetype archetype) throws ParserConfigurationException, SAXException, IOException, DocumentException {
			this.archetype = archetype;
			final SAXReader reader = new SAXReader();
	        document = reader.read(pomFile);
	        document.accept(new VisitorSupport() {
	        	@Override
				public void visit(final Document document) {
	        	    ((DefaultElement) document.getRootElement())
	        	            .setNamespace(Namespace.NO_NAMESPACE);
	        	    document.getRootElement().additionalNamespaces().clear();
	        	}

	        	@Override
				public void visit(final Namespace namespace) {
	        	    if (namespace.getParent() != null) {
	        	        namespace.getParent().remove(namespace);
	        	    }
	        	}

	        	@Override
				public void visit(final Attribute node) {
	        	    if (node.toString().contains("xmlns")
	        	            || node.toString().contains("xsi:")) {
	        	        node.getParent().remove(node);
	        	    }
	        	}

	        	@Override
				public void visit(final Element node) {
	        	    if (node instanceof DefaultElement) {
	        	        ((DefaultElement) node).setNamespace(Namespace.NO_NAMESPACE);
	        	        node.additionalNamespaces().clear();
	        	    }
	        	}
			});
		}

		@Override
		public Optional<String> getJavaVersion() {
			try {
				String javaVersion = "";
				Node sourceNode = document.selectSingleNode("//build/plugins/plugin//configuration/source");
				if (sourceNode != null) {
					javaVersion = sourceNode.getText();
					if (javaVersion.contains("$")) {
						javaVersion = findAsProperty(javaVersion);
					}
				}
				if (javaVersion.isEmpty()) {
					sourceNode = document.selectSingleNode("build/pluginManagement/plugins/plugin//configuration/source");
					if (sourceNode != null) {
						javaVersion = sourceNode.getText();
						if (javaVersion.contains("$")) {
							javaVersion = findAsProperty(javaVersion);
						}
					}
				}
				if (javaVersion.isEmpty()) {
					sourceNode = document.selectSingleNode("properties/maven.compiler.source");
					if (sourceNode != null) {
						javaVersion = sourceNode.getText();
					}
				}
				if (javaVersion.isEmpty()) {
					sourceNode = document.selectSingleNode("properties/java.version");
					if (sourceNode != null) {
						javaVersion = sourceNode.getText();
					}
				}
				if (javaVersion.isEmpty()) {
					return Optional.empty();
				}
				return Optional.of(javaVersion);
			} catch (final Exception e) {
				LOG.error("Could not determine java version", e);
				return Optional.empty();
			}
		}

		private String findAsProperty(final String javaVersion) {
			final String propertyName = javaVersion.substring(2, javaVersion.length() - 1);
			final Node propertyNode = document.selectSingleNode("properties/" + propertyName);
			if (propertyNode != null) {
				return propertyNode.getText();
			}
			return "";
		}

		@Override
		public Optional<String> getPackaging() {
			final Node packagingNode = document.selectSingleNode("packaging");
			if (null != packagingNode) {
				return Optional.of(packagingNode.getText());
			}
			return Optional.of("jar");
		}

		@Override
		public Optional<OffsetDateTime> getLastUpdated() {
	        try {
				final String urlString = StringUtils.substringBeforeLast(archetype.getRepositoryUrl(), "/")
						+ "/"
						+ archetype.getGroupId().replace(".", "/")
						+ "/" + archetype.getArtifactId()
						+ "/"
						+ archetype.getVersion();
				final URLConnection conn = new URL(urlString).openConnection();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
					final String content = reader.lines().collect(Collectors.joining("\n"));
					final Pattern pattern = Pattern.compile("((([0-9])|([0-2][0-9])|([3][0-1]))\\-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\-\\d{4} \\d{2}:\\d{2})");
					final Matcher matcher = pattern.matcher(content);
					if (matcher.find()) {
						final String date = matcher.group(0);
						final DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MMM-yyyy HH:mm")
								.withZone(ZoneId.of("UTC"))
								.withLocale(Locale.US);
						return Optional.of(ZonedDateTime.from(formatter.parse(date)).toOffsetDateTime());
					}
					return Optional.empty();
				}
			} catch (final IOException e) {
				LOG.error("Could not determine last updated.", e);
				return Optional.empty();
			}
		}

	}

	private static class EmptyPomImpl implements Pom {

		@Override
		public Optional<String> getJavaVersion() {
			return Optional.empty();
		}

		@Override
		public Optional<String> getPackaging() {
			return Optional.empty();
		}

		@Override
		public Optional<OffsetDateTime> getLastUpdated() {
			return Optional.empty();
		}

	}

}
