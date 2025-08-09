package com.winternari.sns_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profile_images/";
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profile_images/**")
                .addResourceLocations("file:" + UPLOAD_DIR);
    }
}
