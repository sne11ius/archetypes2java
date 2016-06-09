package es.archetyp.archetypes2.backend.archetype.entity;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import es.archetyp.archetypes2.backend.entity.AbstractBaseEntity;
import es.archetyp.archetypes2.backend.entity.DefaultVisible;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"groupId", "artifactId", "version"})
})
public class Archetype extends AbstractBaseEntity {

	public Archetype() {
	}

	public Archetype(final String groupId, final String artifactId, final String version, final Optional<String> description, final String repository) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.description = description.orElse(null);
		repositoryUrl = repository;
	}

	@DefaultVisible(order = 0)
	@Column(nullable = false)
	private String groupId;

	@DefaultVisible(order = 1)
	@Column(nullable = false)
	private String artifactId;

	@DefaultVisible(order = 2)
	@Column(nullable = false)
	private String version;

	@DefaultVisible(order = 3)
	@Lob
	@Column(nullable = true)
	private String description;

	@Column(nullable = false)
	private String repositoryUrl;

	@DefaultVisible(order = 4)
	@Column(nullable = true)
	private String javaVersion;

	@DefaultVisible(order = 5)
	@Column(nullable = true)
	private String packaging;

	@Column(nullable = true)
	private OffsetDateTime lastUpdated;

	@Column(nullable = true)
	private String localDir;

	@Lob
	@Column(nullable = true)
	private String generateLog;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column()
	private Set<String> additionalProps;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(final String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public void setDescription(final Optional<String> description) {
		this.description = description.orElse(null);
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(final String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public Optional<String> getJavaVersion() {
		return Optional.ofNullable(javaVersion);
	}

	public void setJavaVersion(final Optional<String> javaVersion) {
		this.javaVersion = javaVersion.orElse(null);
	}

	public Optional<String> getPackaging() {
		return Optional.ofNullable(packaging);
	}

	public void setPackaging(final Optional<String> packaging) {
		this.packaging = packaging.orElse(null);
	}

	public Optional<OffsetDateTime> getLastUpdated() {
		return Optional.ofNullable(lastUpdated);
	}

	public void setLastUpdated(final Optional<OffsetDateTime> lastUpdated) {
		this.lastUpdated = lastUpdated.orElse(null);
	}

	public Optional<String> getLocalDir() {
		return Optional.ofNullable(localDir);
	}

	public void setLocalDir(final Optional<String> localDir) {
		this.localDir = localDir.orElse(null);
	}

	public Optional<String> getGenerateLog() {
		return Optional.ofNullable(generateLog);
	}

	public void setGenerateLog(final Optional<String> generateLog) {
		this.generateLog = generateLog.orElse(null);
	}

	public Set<String> getAdditionalProps() {
		return additionalProps;
	}

	public void setAdditionalProps(final Set<String> additionalProps) {
		this.additionalProps = additionalProps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Archetype other = (Archetype) obj;
		if (artifactId == null) {
			if (other.artifactId != null) {
				return false;
			}
		} else if (!artifactId.equals(other.artifactId)) {
			return false;
		}
		if (groupId == null) {
			if (other.groupId != null) {
				return false;
			}
		} else if (!groupId.equals(other.groupId)) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Archetype [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + ", description=" + description + ", repository=" + repositoryUrl + ", javaVersion=" + javaVersion + ", packaging=" + packaging + ", lastUpdated=" + lastUpdated + ", localDir=" + localDir + ", generateLog=" + generateLog + ", additionalProps=" + additionalProps + "]";
	}

}
