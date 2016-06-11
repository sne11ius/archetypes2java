package es.archetyp.archetypes2.gui.archetypes.detail;

import java.time.format.DateTimeFormatter;

import org.springframework.context.event.EventListener;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;

@UIScope
@SpringComponent
public class ArchetypeDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public ArchetypeDetailsPanel() {
		setCaption("Details");
		setWidth(500, Unit.PIXELS);
	}

	@EventListener
	private void onArchetypeDetailChanged(final ArchetypeDetailChangedEvent e) {
		final Archetype archetype = e.getArchetype();
		final GridLayout layout = new GridLayout(2, 7);
		layout.addComponent(new Label("Group Id", ContentMode.HTML), 0, 0);
		layout.addComponent(new Label(archetype.getGroupId()), 1, 0);
		layout.addComponent(new Label("Artifact Id", ContentMode.HTML), 0, 1);
		layout.addComponent(new Label(archetype.getArtifactId()), 1, 1);
		layout.addComponent(new Label("Version", ContentMode.HTML), 0, 2);
		layout.addComponent(new Label(archetype.getVersion()), 1, 2);
		layout.addComponent(new Label("Description", ContentMode.HTML), 0, 3);
		layout.addComponent(new Label(archetype.getDescription().orElse("[none]")), 1, 3);
		layout.addComponent(new Label("Java Version", ContentMode.HTML), 0, 4);
		layout.addComponent(new Label(archetype.getJavaVersion().orElse("[default]")), 1, 4);
		layout.addComponent(new Label("Packaging", ContentMode.HTML), 0, 5);
		layout.addComponent(new Label(archetype.getPackaging().orElse("jar")), 1, 5);
		layout.addComponent(new Label("Release", ContentMode.HTML), 0, 6);
		layout.addComponent(new Label(archetype.getLastUpdated().map(DateTimeFormatter.ISO_OFFSET_DATE_TIME::format).orElse("unknown")), 1, 6);
		layout.setWidth(490, Unit.PIXELS);
		layout.setColumnExpandRatio(0, 0.3F);
		layout.setColumnExpandRatio(1, 0.7F);
		setContent(layout);
	}


}
