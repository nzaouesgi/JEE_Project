package fr.esgi.secureupload.files.infrastructure.converters;

import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import org.springframework.core.convert.converter.Converter;

public class StringToFileOrderByFieldConverter implements Converter<String, FileOrderByField> {
    @Override
    public FileOrderByField convert(String source) {
        return FileOrderByField.valueOf(source.toUpperCase());
    }
}
