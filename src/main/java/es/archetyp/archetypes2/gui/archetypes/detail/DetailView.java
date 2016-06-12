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
	private OtherArchetypeVersionsList olderVersionsList;

	@Autowired
	private ArchetypeExampleFileContentPanel fileContentPanel;

	@Autowired
	private AchetypeSourceTree achetypeSourceTree;

	@Autowired
	private ApplicationEventPublisher publisher;

    @Override
    public void enter(final ViewChangeEvent event) {
    	publishArchetypeDetailChangeEvent(event);
        initLayout();
    }

	private void publishArchetypeDetailChangeEvent(final ViewChangeEvent event) {
		final String[] args = event.getParameters().split("/");
    	final Archetype archetype = archetypes.findByGroupIdAndArtifactIdAndVersion(args[0], args[1], args[2]).orElseThrow(ArchetypeNotFoundException::new);
    	publisher.publishEvent(new ArchetypeDetailChangedEvent(archetype));
	}

	private void initLayout() {
		final VerticalLayout vlayout = new VerticalLayout();
        vlayout.setSizeFull();
        vlayout.setMargin(true);
        vlayout.setSpacing(true);
        final HorizontalLayout hlayout1 = new HorizontalLayout();
        vlayout.addComponent(hlayout1);
        hlayout1.addComponent(detailsPanel);
        hlayout1.addComponent(commandPanel);
        hlayout1.addComponent(olderVersionsList);
        hlayout1.setSpacing(true);
        hlayout1.setWidth(100, Unit.PERCENTAGE);
        final HorizontalLayout hlayout2 = new HorizontalLayout();
        vlayout.addComponent(hlayout2);
        hlayout2.addComponent(achetypeSourceTree);
        hlayout2.addComponent(fileContentPanel);
        hlayout2.setSpacing(true);
        setCompositionRoot(vlayout);
	}

}
