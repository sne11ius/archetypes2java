package es.archetyp.archetypes2.gui.archetypes;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import es.archetyp.archetypes2.archetype.Archetype;
import es.archetyp.archetypes2.gui.messages.Messages;
import lombok.extern.log4j.Log4j2;

@SpringView(name = "list")
@Log4j2
public class ArchetypesListView extends CustomComponent implements View {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Messages messages;

	@Autowired
	private ArchetypesQueryFactory archetypesQueryFactory;

	@PostConstruct
	public void init() {
		final VerticalLayout layout = new VerticalLayout();
		try {
			final Grid grid = createGrid();
			grid.setSizeFull();
			layout.addComponent(grid);
			layout.setSizeFull();
		} catch (final IntrospectionException e) {
			LOG.error("Cannot grid", e);
		}
		setCompositionRoot(layout);
		setSizeFull();
	}

	private Grid createGrid() throws IntrospectionException {
		final Grid grid = new Grid();
		final LazyQueryContainer items = new LazyQueryContainer(archetypesQueryFactory, "id", 100, false);
		for (final PropertyDescriptor desc : Introspector.getBeanInfo(Archetype.class).getPropertyDescriptors()) {
			if (desc.getReadMethod() != null && desc.getWriteMethod() != null) {
				if (desc.getReadMethod().isAnnotationPresent(DefaultVisible.class)) {
					final Class<?> clazz = desc.getPropertyType();
					items.addContainerProperty(desc.getName(), clazz, null);
				}
			}
		}
		grid.setContainerDataSource(items);
		return grid;
	}

	@Override
	public void enter(final ViewChangeEvent event) {

	}

}
