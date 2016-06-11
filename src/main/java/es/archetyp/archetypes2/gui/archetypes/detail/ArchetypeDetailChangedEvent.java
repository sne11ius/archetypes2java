package es.archetyp.archetypes2.gui.archetypes.detail;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;

public class ArchetypeDetailChangedEvent {

	private final Archetype archetype;

	public ArchetypeDetailChangedEvent(final Archetype archetype) {
		this.archetype = archetype;
	}

	public Archetype getArchetype() {
		return archetype;
	}

}
