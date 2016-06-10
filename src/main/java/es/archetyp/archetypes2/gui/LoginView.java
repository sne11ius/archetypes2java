package es.archetyp.archetypes2.gui;

import javax.annotation.PostConstruct;

import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import es.archetyp.archetypes2.gui.messages.Messages;
import lombok.extern.log4j.Log4j2;

@SpringView(name = "login")
@Log4j2
public class LoginView extends CustomComponent implements View {

	private static final long serialVersionUID = 1L;

    @PostConstruct
    public void init() {
    	final DefaultVerticalLoginForm loginForm = createLoginForm();
    	final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(loginForm);
        layout.setSizeFull();
        layout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    	setCompositionRoot(layout);
    	setSizeFull();
    }

	private DefaultVerticalLoginForm createLoginForm() {
		final DefaultVerticalLoginForm loginForm = new DefaultVerticalLoginForm() {
			private static final long serialVersionUID = 1L;
			@Override
    		protected String getUserNameFieldCaption() {
				return Messages.loginUsername();
    		}
    		@Override
    		protected String getPasswordFieldCaption() {
    			return Messages.loginPassword();
    		}
    		@Override
    		protected String getLoginButtonCaption() {
    			return Messages.loginButton();
    		}
    	};
    	loginForm.addLoginListener(event -> {
			LOG.debug("name: " + event.getUserName());
			LOG.debug("pass: " + event.getPassword());
    	});
		return loginForm;
	}

    @Override
    public void enter(final ViewChangeEvent event) {

    }

}
