package com.stefanini.desafio.todolistapi.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        // Define o esquema de segurança Basic Auth para ser usado nos Controllers
        name = "BasicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API To-Do List - Desafio Java")
                        .version("1.0.0")
                        .description(
                                "API RESTful para gerenciamento de tarefas (To-Do List), desenvolvida como desafio técnico. " +
                                        "Utiliza Java 21, Spring Boot 3, Clean Architecture e SQL Server. " +
                                        "Acesso protegido por Basic Authentication."
                        )
                );
    }
}