package es.archetyp.archetypes2.gui.archetypes.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.backend.archetype.entity.Archetypes;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UIScope
@SpringView(name = "detail")
public class DetailView extends CustomComponent implements View {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Archetypes archetypes;

	@Autowired
	private ArchetypeDetailsPanel detailsPanel;

	@Autowired
	private ArchetypeCommandPanel commandPanel;

	@Autowired
	private ApplicationEventPublisher publisher;

    @Override
    public void enter(final ViewChangeEvent event) {
        final VerticalLayout vlayout = new VerticalLayout();
        vlayout.setSizeFull();
        vlayout.setMargin(true);
        vlayout.setSpacing(true);
        final HorizontalLayout hlayout = new HorizontalLayout();
        vlayout.addComponent(hlayout);
        final String[] args = event.getParameters().split("/");
        final Archetype archetype = archetypes.findByGroupIdAndArtifactIdAndVersion(args[0], args[1], args[2]).orElseThrow(ArchetypeNotFoundException::new);
        publisher.publishEvent(new ArchetypeDetailChangedEvent(archetype));
        hlayout.addComponent(detailsPanel);
        hlayout.addComponent(commandPanel);
        hlayout.setExpandRatio(commandPanel, 1.0F);
        hlayout.setSpacing(true);
        hlayout.setWidth(100, Unit.PERCENTAGE);
        setCompositionRoot(vlayout);
    }

}
