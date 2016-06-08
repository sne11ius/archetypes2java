package es.archetyp.archetypes2.archetype;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface Pom {

	Optional<String> getJavaVersion();

	Optional<String> getPackaging();

	Optional<OffsetDateTime> getLastUpdated();

}
