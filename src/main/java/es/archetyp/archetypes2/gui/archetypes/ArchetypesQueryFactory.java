package es.archetyp.archetypes2.gui.archetypes;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;
import es.archetyp.archetypes2.backend.archetype.boundary.ArchetypeService;
import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ArchetypesQueryFactory implements QueryFactory {

	@Autowired
	private ArchetypeService archetypesService;

	@PostConstruct
	public void init() {
		LOG.debug("findNewest...");
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
				return archetypesService.getNewest().subList(startIndex, startIndex + count);
			}

			@Override
			protected void saveBeans(final List<Archetype> arg0, final List<Archetype> arg1, final List<Archetype> arg2) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int size() {
				return archetypesService.getNewest().size();
			}
		};
	}

}
