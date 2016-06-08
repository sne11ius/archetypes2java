package es.archetyp.archetypes2.gui;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

@Component
@UIScope
public class NavigationService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private ErrorView errorView;

    @Autowired
    private UI ui;

    @PostConstruct
    public void initialize() {
        if (ui.getNavigator() == null) {
            final Navigator navigator = new Navigator(ui, ui);
            navigator.addProvider(viewProvider);
            navigator.setErrorView(errorView);
        }
    }

	@EventListener
	public void handleNavigationEvent(final NavigationEvent event) {
		try {
            ui.getNavigator().navigateTo(event.getNavigateTo());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
	}

}
