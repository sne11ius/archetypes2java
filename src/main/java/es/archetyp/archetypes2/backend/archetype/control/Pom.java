package es.archetyp.archetypes2.backend.archetype.control;

import java.time.OffsetDateTime;
import java.util.Optional;

interface Pom {

	Optional<String> getJavaVersion();

	Optional<String> getPackaging();

	Optional<OffsetDateTime> getLastUpdated();

}
