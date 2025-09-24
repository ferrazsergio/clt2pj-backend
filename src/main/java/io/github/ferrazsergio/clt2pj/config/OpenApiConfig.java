package io.github.ferrazsergio.clt2pj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Simulação CLT/PJ")
                        .version("1.0.0")
                        .description("API para simular comparativos financeiros entre CLT e PJ")
                );
    }
}