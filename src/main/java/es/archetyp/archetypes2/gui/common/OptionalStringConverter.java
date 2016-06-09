package es.archetyp.archetypes2.gui.common;

import java.util.Locale;
import java.util.Optional;
import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("rawtypes")
public class OptionalStringConverter implements Converter<String, Optional> {
	private static final long serialVersionUID = 1L;

	@Override
	public Optional convertToModel(final String value, final Class<? extends Optional> targetType, final Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		return Optional.ofNullable(value);
	}

	@Override
	public String convertToPresentation(final Optional value, final Class<? extends String> targetType, final Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value.isPresent()) {
			return value.get().toString();
		}
		return "";
	}

	@Override
	public Class<Optional> getModelType() {
		return Optional.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
