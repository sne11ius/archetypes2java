package es.archetyp.archetypes2.gui.common;

import static es.archetyp.archetypes2.util.CollectionUtil.emptyList;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.util.ReflectionUtils;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ButtonRenderer;

import es.archetyp.archetypes2.backend.archetype.entity.Archetype;
import es.archetyp.archetypes2.backend.entity.AbstractBaseEntity;
import es.archetyp.archetypes2.backend.entity.DefaultVisible;
import es.archetyp.archetypes2.gui.messages.Messages;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AutoGrid<T extends AbstractBaseEntity> extends Grid {

	private static final long serialVersionUID = 1L;
	private final Class<T> type;
	private final BeanItemContainer<T> dataSource;
	private final Optional<Consumer<T>> rowAction;

	public AutoGrid(final Class<T> type, final SortableBeanItemContainer<T> dataSource, final Optional<Consumer<T>> rowAction) {
		super(dataSource);
		this.type = type;
		this.dataSource = dataSource;
		this.rowAction = rowAction;
		autoConfigure();
	}

	private void autoConfigure() {
		try {
			final List<Field> fields = emptyList();
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
									fields.add(field);
								} else {
									column.setHidden(true);
								}
							} else {
								fields.add(field);
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
			Collections.sort(fields, new DefaultVisibleOrderComparator());
			setColumnOrder(fields.stream().map(f -> f.getName()).toArray());
			final HeaderRow filterRow = appendHeaderRow();

			setFrozenColumnCount(frozenColumns);

			for (int i = frozenColumns; i < fields.size(); ++i) {
			//for (final Object pid : getContainerDataSource().getContainerPropertyIds()) {
				final String pid = fields.get(i).getName();
				final HeaderCell cell = filterRow.getCell(pid);

				final TextField filterField = new TextField();
				filterField.setSizeFull();
				filterField.setHeight(70, Unit.PERCENTAGE);

				filterField.addTextChangeListener(change -> {
					dataSource.removeContainerFilters(pid);

					if (!change.getText().isEmpty()) {
						dataSource.addContainerFilter(new SimpleStringFilter(pid, change.getText(), true, false));
					}
				});
				cell.setComponent(filterField);
			}
		}
		catch (final UnsupportedOperationException | IntrospectionException e) {
			LOG.error("Could not autoconfig.", e);
		}
	}

}
