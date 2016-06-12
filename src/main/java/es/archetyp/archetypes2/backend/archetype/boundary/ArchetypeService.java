package es.archetyp.archetypes2.backend.archetype.boundary;

import static es.archetyp.archetypes2.util.CollectionUtil.copy;
import static es.archetyp.archetypes2.util.CollectionUtil.emptyList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.backend.archetype.entity.Archetypes;

@Service
public class ArchetypeService {

	@Autowired
	private Archetypes archetypes;

	private List<Archetype> newestArchetypes = emptyList();

	@PostConstruct
	public void init() {
		newestArchetypes = loadNewestArchetypes();
	}

	@EventListener
	private void onArchetypesDatabaseUpdated(final ArchetypesDatabaseUpdatedEvent e) {
		newestArchetypes = loadNewestArchetypes();
	}

	public List<Archetype> getNewest() {
		return copy(newestArchetypes);
	}

	private List<Archetype> loadNewestArchetypes() {
		return archetypes.findAll()
			.parallelStream()
			.collect(Collectors.groupingBy(a -> ImmutablePair.of(a.getGroupId(), a.getArtifactId())))
			.values()
			.stream()
			.map(l -> {
				Collections.sort(l, new Comparator<Archetype>() {
					@Override
					public int compare(final Archetype o1, final Archetype o2) {
						return - new ComparableVersion(o1.getVersion()).compareTo(new ComparableVersion(o2.getVersion()));
					}
				});
				return l.get(0);
			}).collect(Collectors.toList());
	}

	public String createGenerateCommand(final Archetype archetype) {
		return "mvn archetype:generate -DgroupId=com.example.app -DartifactId=example-app -Dversion=1.0-SNAPSHOT -Dpackage=com.example.app -DinteractiveMode=false" +
					" -DarchetypeGroupId=" + archetype.getGroupId() +
					" -DarchetypeArtifactId=" + archetype.getArtifactId() +
					" -DarchetypeVersion=" + archetype.getVersion() +
					archetype.getAdditionalProps()
						.stream()
						.map(p -> " " + p + "=My" + StringUtils.capitalize(p))
						.collect(Collectors.joining());
	}

	public List<Archetype> findOlderVersions(final Archetype archetype) {
		final List<Archetype> allVersions = archetypes.findByGroupIdAndArtifactId(archetype.getGroupId(), archetype.getArtifactId());
		allVersions.remove(archetype);
		return allVersions;
	}

}
