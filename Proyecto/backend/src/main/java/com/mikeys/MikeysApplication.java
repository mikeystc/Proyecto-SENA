package com.mikeys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase principal de la aplicación Mikeys
 * Arranca el servidor embebido Tomcat y configura Spring Boot
 */
@SpringBootApplication
public class MikeysApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(MikeysApplication.class, args);
        System.out.println("=================================");
        System.out.println("Mikeys Backend Started Successfully!");
        System.out.println("Server running on port: 8080");
        System.out.println("=================================");
    }

    /**
     * Configuración CORS para permitir peticiones desde el frontend
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8081", "file://")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}