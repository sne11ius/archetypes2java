package es.archetyp.archetypes2.gui.archetypes.detail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.context.event.EventListener;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import lombok.extern.log4j.Log4j2;

@Log4j2
@UIScope
@SpringComponent
public class ArchetypeExampleFileContentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public ArchetypeExampleFileContentPanel() {
		setCaption("File Content");
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeExampleFileSelectedEvent event) {
		try {
			setContent(new Label("<pre>" + StringEscapeUtils.escapeHtml4(IOUtils.toString(event.getFile().toURI(), StandardCharsets.UTF_8)) + "</pre>", ContentMode.HTML));
		} catch (final IOException e) {
			LOG.error("Could not load file.", e);
		}
	}
}
