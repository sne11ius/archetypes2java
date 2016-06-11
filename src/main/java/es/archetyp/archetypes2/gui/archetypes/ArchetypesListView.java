package es.archetyp.archetypes2.gui.archetypes;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import es.archetyp.archetypes2.backend.archetype.boundary.ArchetypeService;
import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.gui.NavigationEvent;
import es.archetyp.archetypes2.gui.common.AutoGrid;
import es.archetyp.archetypes2.gui.common.SortableBeanItemContainer;
import lombok.extern.log4j.Log4j2;

@SpringView(name = "list")
@Log4j2
public class ArchetypesListView extends CustomComponent implements View {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ArchetypeService archetypeService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@PostConstruct
	public void init() {
		final VerticalLayout layout = new VerticalLayout();
		final Grid grid = createGrid();
		grid.setSizeFull();
		layout.addComponent(grid);
		layout.setSizeFull();
		setCompositionRoot(layout);
		setSizeFull();
	}

	private Grid createGrid() {
		return new AutoGrid<>(
			Archetype.class,
			new SortableBeanItemContainer<>(Archetype.class, archetypeService.getNewest()),
			Optional.of(a -> {
				publisher.publishEvent(new NavigationEvent("detail/" + a.getGroupId() + "/" + a.getArtifactId() + "/" + a.getVersion()));
			})
		);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		// empty
	}

}
