package com.example.fluere.bootloader.config;

import com.example.fluere.FluereApplicationDev;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@ConditionalOnProperty(name = "app.property.service.bootloader-info", havingValue = "true")
public class ServletInitializerDev extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FluereApplicationDev.class);
    }
}
