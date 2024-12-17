package com.decoder.aiquizzer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        return new OpenAPI()
                .info(new Info()
                        .title("AI Quizzer")
                        .description("By Ketan Sondarava (decoder) \n\n\n\n\n\n Note: only 1 endpoint is public (use it for login), all other are private endpoints (use after login)")
                        .version("1.0.0"))
                .tags(List.of(
                        new Tag().name("Authentication endpoint (public)")
                ))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
