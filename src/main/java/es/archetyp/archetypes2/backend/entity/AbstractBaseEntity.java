package es.archetyp.archetypes2.backend.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractBaseEntity {

	@DefaultVisible(order = -1)
	@Transient
	private final String sortProperty = null;

	@Id
    @GeneratedValue
    private Long id;

	public String getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(final String sortProperty) {
	}

}
