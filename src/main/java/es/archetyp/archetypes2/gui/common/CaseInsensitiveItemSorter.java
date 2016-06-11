package es.archetyp.archetypes2.gui.common;

import java.io.Serializable;
import java.util.Comparator;

import com.vaadin.data.util.DefaultItemSorter;

public class CaseInsensitiveItemSorter extends DefaultItemSorter {

	private static final long serialVersionUID = 1L;

    public CaseInsensitiveItemSorter() {
        super(new CaseInsensitivePropertyValueComparator());
    }

    public static class CaseInsensitivePropertyValueComparator implements Comparator<Object>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
        public int compare(final Object o1, final Object o2) {
			if (o1 instanceof Comparable) {
				return ((Comparable)o1).compareTo(o2);
			}
			return (o1 == null ? "" : o1.toString()).toLowerCase().compareTo(o2 == null ? "" : o2.toString().toLowerCase());
        }
    }
}
