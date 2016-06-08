package es.archetyp.archetypes2.gui.archetypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import es.archetyp.archetypes2.archetype.Archetype;
import es.archetyp.archetypes2.archetype.ArchetypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ArchetypesQueryFactory implements QueryFactory {

	@Autowired
	ArchetypeRepository archetypesRepository;

	private List<Archetype> newest;

	@PostConstruct
	public void init() {
		LOG.debug("findNewest...");
		newest = findNewest();
		LOG.debug("...done");
	}

	@Override
	public Query constructQuery(final QueryDefinition queryDefinition) {
		return new AbstractBeanQuery<Archetype>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Archetype constructBean() {
				return new Archetype();
			}

			@Override
			protected List<Archetype> loadBeans(final int startIndex, final int count) {
				return newest.subList(startIndex, startIndex + count);
			}

			@Override
			protected void saveBeans(final List<Archetype> arg0, final List<Archetype> arg1, final List<Archetype> arg2) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return newest.size();
			}
		};
	}

	private List<Archetype> findNewest() {
		return archetypesRepository.findAll()
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
