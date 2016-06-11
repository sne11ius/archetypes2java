package es.archetyp.archetypes2.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface CollectionUtil {

	public static <T> List<T> emptyList() {
		return new ArrayList<>();
	}

	public static <T> List<T> copy(final List<T> original) {
		return new ArrayList<>(original);
	}

	public static <T> List<T> toList(final Collection<T> original) {
		return new ArrayList<>(original);
	}

}
