package fr.esgi.secureupload.common.infrastructure.config;

import fr.esgi.secureupload.common.infrastructure.converters.StringToOrderModeConverter;
import fr.esgi.secureupload.users.infrastructure.converters.StringToUserOrderByFieldConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToOrderModeConverter());
        registry.addConverter(new StringToUserOrderByFieldConverter());
    }
}
