package es.archetyp.archetypes2.gui.archetypes.detail;

import java.io.File;

public class ArchetypeExampleFileSelectedEvent  {

	private final File file;

	public ArchetypeExampleFileSelectedEvent(final File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

}
