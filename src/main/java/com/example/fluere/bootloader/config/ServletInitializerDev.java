package com.example.fluere.bootloader.config;

import com.example.fluere.FluereApplicationDev;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Profile;

@Profile({"dev", "test"})
//TODO change this config for the profiles. Separate for test, dev and prod
public class ServletInitializerDev extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(FluereApplicationDev.class);
	}
}
