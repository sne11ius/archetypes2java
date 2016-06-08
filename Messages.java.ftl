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

	<#list messages as message>public String ${message.methodName}() {
		return messageSource.getMessage("${message.key}", null, locale);
	}

	</#list>

}
