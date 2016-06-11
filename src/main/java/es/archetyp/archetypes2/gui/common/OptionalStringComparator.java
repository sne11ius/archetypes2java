package es.archetyp.archetypes2.gui.common;

import java.util.Comparator;

public class OptionalStringComparator implements Comparator<Object> {

	@Override
	public int compare(final Object o1, final Object o2) {
		return o1.toString().compareTo(o2.toString());
	}

}
