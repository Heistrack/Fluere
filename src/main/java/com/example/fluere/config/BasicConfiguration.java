package com.example.fluere.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                         .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                         .findAndAddModules()
                         .build();
    }
}
