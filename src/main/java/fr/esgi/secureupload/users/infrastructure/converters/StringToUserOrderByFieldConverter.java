package fr.esgi.secureupload.users.infrastructure.converters;

import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import org.springframework.core.convert.converter.Converter;

public class StringToUserOrderByFieldConverter implements Converter<String, UserOrderByField> {
    @Override
    public UserOrderByField convert(String source) {
        return UserOrderByField.valueOf(source.toUpperCase());
    }
}