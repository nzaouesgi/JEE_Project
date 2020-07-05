package fr.esgi.secureupload.common.infrastructure.config;

import fr.esgi.secureupload.common.infrastructure.converters.StringToOrderModeConverter;
import fr.esgi.secureupload.files.infrastructure.converters.StringToFileOrderByFieldConverter;
import fr.esgi.secureupload.users.infrastructure.converters.StringToUserOrderByFieldConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToOrderModeConverter());
        registry.addConverter(new StringToUserOrderByFieldConverter());
        registry.addConverter(new StringToFileOrderByFieldConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON);
    }
}
