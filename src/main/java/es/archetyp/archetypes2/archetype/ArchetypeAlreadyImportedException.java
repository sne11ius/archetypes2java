package es.archetyp.archetypes2.archetype;

public class ArchetypeAlreadyImportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Archetype archetype;

	public ArchetypeAlreadyImportedException(final Archetype archetype) {
		this.archetype = archetype;
	}

	@Override
	public String getMessage() {
		return "Archetype already imported: " + archetype;
	}

}
