package es.archetyp.archetypes2.gui.archetypes.detail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import es.archetyp.archetypes2.backend.code.boundary.CodeHighlighterService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UIScope
@SpringComponent
public class ArchetypeExampleFileContentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CodeHighlighterService highlighterService;

	public ArchetypeExampleFileContentPanel() {
		setCaption("File Content");
	}
	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeDetailChangedEvent e) {
		setContent(new Label("Select a file to show its contents."));
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeExampleFileSelectedEvent event) {
		try {
			final String fileContent = IOUtils.toString(event.getFile().toURI(), StandardCharsets.UTF_8);
			final String highlightedCode = highlighterService.highlightCode(fileContent, event.getFile().getAbsolutePath());
			setContent(new Label(highlightedCode, ContentMode.HTML));
		} catch (final IOException e) {
			LOG.error("Could not load file.", e);
		}
	}
}
