package br.com.fiap.Aprenda.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Aprenda+ API")
                                                .version("1.0.0")
                                                .description("API RESTful para a plataforma educacional gamificada Aprenda+")
                                                .contact(new Contact()
                                                                .name("Equipe Aprenda+")
                                                                .email("contato@aprendaplus.com"))
                                                .license(new License()
                                                                .name("MIT")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                                .components(new Components()
                                                .addSecuritySchemes("basicAuth", new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("basic")
                                                                .description("HTTP Basic Authentication - Use email e senha do usuário")));
        }
}
