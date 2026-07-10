package org.workshop.automanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoManager API")
                        .description("API REST para gestão de oficina automotiva")
                        .version("v1")
                        .contact(new Contact().name("AutoManager")));
    }
}
