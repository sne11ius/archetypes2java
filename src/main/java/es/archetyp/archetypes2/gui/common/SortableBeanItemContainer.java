package es.archetyp.archetypes2.gui.common;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.DefaultItemSorter.DefaultPropertyValueComparator;

import es.archetyp.archetypes2.backend.entity.AbstractBaseEntity;

public class SortableBeanItemContainer<T extends AbstractBaseEntity> extends BeanItemContainer<T> {

	private static final long serialVersionUID = 1L;

	public SortableBeanItemContainer(final Class<? super T> type, final Collection<? extends T> collection) throws IllegalArgumentException {
		super(type, collection);
		setItemSorter(new DefaultItemSorter(new DefaultPropertyValueComparator() {
			private static final long serialVersionUID = 1L;

			@Override
			public int compare(final Object o1, final Object o2) {
				if (o1 instanceof Optional && o2 instanceof Optional) {
					return Optional.ofNullable(o1).orElse("").toString().toLowerCase(Locale.US).compareTo(Optional.ofNullable(o2).orElse("").toString().toLowerCase(Locale.US));
				} else if (!(o1 instanceof Comparable) || !(o2 instanceof Comparable)) {
					return 0;
				}
				return super.compare(o1, o2);
			}
		}));
	}

	public List<T> getAllItems() {
		return getAllItemIds()
			.stream()
			.map(this::getItem)
			.map(i -> i.getBean())
			.collect(Collectors.toList());
	}

	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		return getContainerPropertyIds();
	}

}
