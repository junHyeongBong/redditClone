package com.dante.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Reddit Clone api")
                        .description("Api for reddit clone application")
                        .version("v0.0.1")
                        .license(new License().name("Apache License version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                    .description("Api for reddit clone application").url("https://www.reddit.com")
                );
    }
}
