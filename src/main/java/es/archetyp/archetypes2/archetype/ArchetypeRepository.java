package es.archetyp.archetypes2.archetype;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ArchetypeRepository extends Repository<Archetype, Long> {

	Archetype save(Archetype archetype);

	Optional<Archetype> findByGroupIdAndArtifactIdAndVersion(String groupId, String artifactId, String version);

	int count();

	Page<Archetype> findAll(Pageable pageable);

	List<Archetype> findAll();

}
