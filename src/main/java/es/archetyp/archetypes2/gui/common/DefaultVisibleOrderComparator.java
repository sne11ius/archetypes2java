package es.archetyp.archetypes2.gui.common;

import java.lang.reflect.Field;
import java.util.Comparator;
import es.archetyp.archetypes2.backend.entity.DefaultVisible;

public class DefaultVisibleOrderComparator implements Comparator<Field> {

	@Override
	public int compare(Field o1, Field o2) {
		return o1.getAnnotation(DefaultVisible.class).order() - o2.getAnnotation(DefaultVisible.class).order();
	}

}
