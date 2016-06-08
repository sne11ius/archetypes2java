package es.archetyp.archetypes2.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@UIScope
@SpringView(name = "error")
public class ErrorView extends CustomComponent implements View {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ApplicationEventPublisher publisher;

    @Override
    public void enter(final ViewChangeEvent event) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(new Label("Unfortunately, the page you've requested does not exists."));
        layout.addComponent(new Button("To login page", e -> {
        	publisher.publishEvent(new NavigationEvent("login"));
        }));
        setCompositionRoot(layout);
    }

}
