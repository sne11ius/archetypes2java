package es.archetyp.archetypes2.gui.common;

import static es.archetyp.archetypes2.util.CollectionUtil.emptyList;
import static es.archetyp.archetypes2.util.CollectionUtil.toList;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.ReflectionUtils;
import org.vaadin.gridutil.cell.GridCellFilter;

import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ButtonRenderer;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.backend.entity.AbstractBaseEntity;
import es.archetyp.archetypes2.backend.entity.DefaultVisible;
import es.archetyp.archetypes2.backend.entity.FilterType;
import es.archetyp.archetypes2.gui.messages.Messages;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AutoGrid<T extends AbstractBaseEntity> extends Grid {

	private static final long serialVersionUID = 1L;
	private final Class<T> type;
	private final SortableBeanItemContainer<T> dataSource;
	private final Optional<Consumer<T>> rowAction;

	public AutoGrid(final Class<T> type, final SortableBeanItemContainer<T> dataSource, final Optional<Consumer<T>> rowAction) {
		super(dataSource);
		this.type = type;
		this.dataSource = dataSource;
		this.rowAction = rowAction;
		autoConfigure();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void autoConfigure() {
		try {
			final List<ImmutablePair<PropertyDescriptor, Field>> fields = emptyList();
			int frozenColumns = 0;
			for (final PropertyDescriptor desc : Introspector.getBeanInfo(Archetype.class).getPropertyDescriptors()) {
				if (desc.getReadMethod() != null && desc.getWriteMethod() != null) {
					final String fieldName = desc.getName();
					final Field field = ReflectionUtils.findField(type, fieldName);
					if (field != null) {
						final Column column = getColumn(fieldName);
						if (field.isAnnotationPresent(DefaultVisible.class)) {
							if (field.getAnnotation(DefaultVisible.class).order() < 0) {
								if (rowAction.isPresent()) {
									column.setRenderer(new ButtonRenderer(e -> {
										e.getItemId();
										rowAction.get().accept(dataSource.getItem(e.getItemId()).getBean());
									}, Messages.autogridAction()));
									column.setHeaderCaption("");
									frozenColumns++;
									fields.add(ImmutablePair.of(desc, field));
								} else {
									column.setHidden(true);
								}
							} else {
								fields.add(ImmutablePair.of(desc, field));
								column.setResizable(true);
								if (Optional.class.isAssignableFrom(desc.getReadMethod().getReturnType())) {
									column.setConverter(new OptionalStringConverter());
								}
								column.setSortable(true);
							}
						} else {
							column.setHidden(true);
						}
					}
				}
			}
			dataSource.setItemSorter(new CaseInsensitiveItemSorter());
			Collections.sort(fields, new DefaultVisibleOrderComparator());
			setColumnOrder(fields.stream().map(f -> f.getRight().getName()).toArray());
			setFrozenColumnCount(frozenColumns);

			final GridCellFilter filter = new GridCellFilter(this);
			for (int i = frozenColumns; i < fields.size(); ++i) {
				final ImmutablePair<PropertyDescriptor, Field> pair = fields.get(i);
				final Field field = pair.getRight();
				final String id = field.getName();
				final FilterType filterType = field.getAnnotation(FilterType.class);
				if (filterType != null) {
					field.setAccessible(true);
					switch (filterType.type()) {
						case TEXT: {
							filter.setTextFilter(id, true, false);
							break;
						}
						case COMBOBOX: {
							final Set<Object> values = dataSource.getAllItems()
								.parallelStream()
								.map(item -> {
									try {
										final Object value = pair.getLeft().getReadMethod().invoke(item);
										//return Optional.ofNullable(value);
										return value;
									} catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
										throw new RuntimeException(e);
									}
								}).collect(Collectors.toSet());
							values.remove("");
							values.remove(Optional.empty());
							final List<Object> list = toList(values);
							Collections.sort(list, new OptionalStringComparator());
							final ComboBox comboBox = filter.setComboBoxFilter(id, list);
							comboBox.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
							for (final Object object : list) {
								comboBox.setItemCaption(object, object instanceof Optional ? ((Optional)object).orElse("").toString() : object.toString());
							}
							break;
						}
						default:
							throw new RuntimeException("Unkown filter type: " + filterType.type());
					}
				}
			}
		}
		catch (final UnsupportedOperationException | IntrospectionException e) {
			LOG.error("Could not autoconfig.", e);
		}
	}

}
