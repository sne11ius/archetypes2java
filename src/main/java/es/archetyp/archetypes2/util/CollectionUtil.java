package es.archetyp.archetypes2.util;

import java.util.ArrayList;
import java.util.List;

public interface CollectionUtil {

	public static <T> List<T> emptyList() {
		return new ArrayList<T>();
	}

	public static <T> List<T> copy(final List<T> original) {
		return new ArrayList<>(original);
	}

}
