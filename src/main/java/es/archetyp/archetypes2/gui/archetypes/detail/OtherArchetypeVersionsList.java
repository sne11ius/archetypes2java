package es.archetyp.archetypes2.gui.archetypes.detail;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import es.archetyp.archetypes2.backend.archetype.boundary.ArchetypeService;
import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.gui.NavigationEvent;
import es.archetyp.archetypes2.gui.common.AutoGrid;
import es.archetyp.archetypes2.gui.common.SortableBeanItemContainer;

@UIScope
@SpringComponent
public class OtherArchetypeVersionsList extends Panel {

	private static final long serialVersionUID = 1L;

	@Autowired
	ArchetypeService archetypeService;

	@Autowired
	private ApplicationEventPublisher publisher;

	public OtherArchetypeVersionsList() {
		setCaption("Other versions");
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeDetailChangedEvent e) {
		final List<Archetype> olderVersions = archetypeService.findOlderVersions(e.getArchetype());
		if (olderVersions.isEmpty()) {
			setContent(new Label("No other versions of this archetype exists"));
		} else {
			final Grid grid = createGrid(olderVersions);
			grid.getColumn("groupId").setHidden(true);
			grid.getColumn("artifactId").setHidden(true);
			grid.setSizeFull();
			setContent(grid);
		}
	}

	private Grid createGrid(final List<Archetype> olderVersions) {
		return new AutoGrid<>(
			Archetype.class,
			new SortableBeanItemContainer<>(Archetype.class, olderVersions),
			Optional.of(a -> {
				publisher.publishEvent(new NavigationEvent("detail/" + a.getGroupId() + "/" + a.getArtifactId() + "/" + a.getVersion()));
			})
		);
	}

}
