package com.hydroneo.aggregateService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // allow all paths
                        .allowedOrigins("http://localhost:4200") // allow only Angular app
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // allow these HTTP methods
                        .allowedHeaders("*") // allow all headers
                        .allowCredentials(true); // allow cookies/auth if needed
            }
        };
    }
}
