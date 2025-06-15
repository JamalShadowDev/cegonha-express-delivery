package br.com.cegonhaexpress.cegonha_express.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    CorsRegistration corsConfig = registry.addMapping("/api/**");
    corsConfig.allowedOrigins(
        "http://localhost:5173",
        "http://localhost:3000",
        "https://localhost:5173",
        "https://localhost:3000");
    corsConfig.allowedMethods("GET", "POST", "PUT");
  }
}
