package es.archetyp.archetypes2.backend.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractBaseEntity {

	@Id
    @GeneratedValue
    private Long id;

}
