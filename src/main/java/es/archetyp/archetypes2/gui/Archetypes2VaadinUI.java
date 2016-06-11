package es.archetyp.archetypes2.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@Theme("archetypes2")
@Widgetset("Archetypes2Widgetset")
@SpringUI
public class Archetypes2VaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ApplicationEventPublisher publisher;

    @Override
    protected void init(final VaadinRequest request) {
    	final String fragment = Page.getCurrent().getUriFragment();
    	if (fragment == null) {
    		publisher.publishEvent(new NavigationEvent("list"));
    	} else {
    		publisher.publishEvent(new NavigationEvent(fragment.substring(1)));
    	}
    }

}
