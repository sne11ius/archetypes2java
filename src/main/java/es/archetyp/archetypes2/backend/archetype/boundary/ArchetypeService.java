package es.archetyp.archetypes2.backend.archetype.boundary;

import static es.archetyp.archetypes2.util.CollectionUtil.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
	public void onArchetypesDatabaseUpdated(@SuppressWarnings("unused") final ArchetypesDatabaseUpdatedEvent e) {
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

}
