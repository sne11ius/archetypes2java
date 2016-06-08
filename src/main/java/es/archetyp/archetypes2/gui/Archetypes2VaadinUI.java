package es.archetyp.archetypes2.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@Theme("valo")
@Widgetset("Archetypes2Widgetset")
@SpringUI
public class Archetypes2VaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ApplicationEventPublisher publisher;

    @Override
    protected void init(final VaadinRequest request) {
    	publisher.publishEvent(new NavigationEvent("login"));
    }

}
