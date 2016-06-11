package es.archetyp.archetypes2.gui.messages;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class Messages {

	private static final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	private static final Locale locale;

	static {
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setUseCodeAsDefaultMessage(true);
		locale = Locale.GERMAN;
	}

	<#list messages as message>
	public static String ${message.methodName}() {
		return messageSource.getMessage("${message.key}", null, locale);
	}
	</#list>

}
