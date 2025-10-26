package com.example.arysu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapeo de URLs a vistas sin pasar por un controlador
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("auth/login");
        registry.addViewController("/error/403").setViewName("error/403");
        registry.addRedirectViewController("/inicio", "/");
    }
}