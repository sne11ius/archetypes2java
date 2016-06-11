package es.archetyp.archetypes2.gui.archetypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import es.archetyp.archetypes2.backend.archetype.boundary.ArchetypeService;

@UIScope
@SpringComponent
public class ArchetypeCommandPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@Autowired
	ArchetypeService archetypeService;

	public ArchetypeCommandPanel() {
		setCaption("Command");
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeDetailChangedEvent e) {
		setContent(new Label("<code>" + archetypeService.createGenerateCommand(e.getArchetype()) + "</code>", ContentMode.HTML));
	}

}
