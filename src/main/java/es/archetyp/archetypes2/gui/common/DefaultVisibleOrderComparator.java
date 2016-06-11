package es.archetyp.archetypes2.gui.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Comparator;

import org.apache.commons.lang3.tuple.ImmutablePair;

import es.archetyp.archetypes2.backend.entity.DefaultVisible;

public class DefaultVisibleOrderComparator implements Comparator<ImmutablePair<PropertyDescriptor, Field>> {

	@Override
	public int compare(final ImmutablePair<PropertyDescriptor, Field> o1, final ImmutablePair<PropertyDescriptor, Field> o2) {
		return o1.getRight().getAnnotation(DefaultVisible.class).order() - o2.getRight().getAnnotation(DefaultVisible.class).order();
	}

}
