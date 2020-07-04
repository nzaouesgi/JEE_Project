package fr.esgi.secureupload.common.infrastructure.converters;

import fr.esgi.secureupload.common.domain.entities.OrderMode;
import org.springframework.core.convert.converter.Converter;

public class StringToOrderModeConverter implements Converter<String, OrderMode> {
    @Override
    public OrderMode convert(String source) {
        return OrderMode.valueOf(source.toUpperCase());
    }
}