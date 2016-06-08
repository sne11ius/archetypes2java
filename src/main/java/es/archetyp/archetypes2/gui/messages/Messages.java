package es.archetyp.archetypes2.gui.messages;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class Messages {

	private final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	private Locale locale;

	@PostConstruct
	public void init() {
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setUseCodeAsDefaultMessage(true);
		locale = Locale.GERMAN;
	}

	public String loginUsername() {
		return messageSource.getMessage("login.username", null, locale);
	}

public String loginPassword() {
		return messageSource.getMessage("login.password", null, locale);
	}

public String loginButton() {
		return messageSource.getMessage("login.button", null, locale);
	}

public String lolwutTest() {
		return messageSource.getMessage("lolwut.test", null, locale);
	}


}
