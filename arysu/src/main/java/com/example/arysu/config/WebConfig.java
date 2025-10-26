package com.example.arysu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Habilita la carga de recursos estáticos (CSS, JS, imágenes)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}