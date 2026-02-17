package com.brett.mypassport.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myPassportOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MyPassport API")
                        .description("User Center Interface API Documentation")
                        .version("v0.0.1")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));

    }

    @Bean
    public OperationCustomizer orderOperationCustomizer() {
        return (operation, handlerMethod) -> {
            Order order = handlerMethod.getMethodAnnotation(Order.class);
            if (order != null) {
                operation.addExtension("x-order", order.value());
            }
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer orderingOpenApiCustomiser() {
        return openApi -> {
            Paths paths = openApi.getPaths().entrySet().stream()
                    .sorted(Comparator.comparing(entry -> getOrder(entry.getValue())))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue,
                            Paths::new
                    ));
            openApi.setPaths(paths);
        };
    }

    private int getOrder(PathItem pathItem) {
        return pathItem.readOperations().stream()
                .map(operation -> {
                    Map<String, Object> extensions = operation.getExtensions();
                    Object order = extensions != null ? extensions.get("x-order") : null;
                    return order != null ? (int) order : Integer.MAX_VALUE;
                })
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }
}
