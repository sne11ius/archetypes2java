package es.archetyp.archetypes2.archetype;

import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ArchetypeRepository extends Repository<Archetype, Long> {

	public Archetype save(Archetype archetype);

	public Optional<Archetype> findByGroupIdAndArtifactIdAndVersion(String groupId, String artifactId, String version);

}
