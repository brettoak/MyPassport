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
            if (order == null) {
                order = handlerMethod.getBeanType().getAnnotation(Order.class);
            }
            if (order != null) {
                operation.addExtension("x-order", order.value());
            }
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer orderingOpenApiCustomiser() {
        return openApi -> {
            // 1. Gather all tags if not explicitly defined at root
            if (openApi.getTags() == null) {
                java.util.List<io.swagger.v3.oas.models.tags.Tag> tags = new java.util.ArrayList<>();
                openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(op -> {
                    if (op.getTags() != null) {
                        op.getTags().forEach(tagName -> {
                            if (tags.stream().noneMatch(t -> t.getName().equals(tagName))) {
                                tags.add(new io.swagger.v3.oas.models.tags.Tag().name(tagName));
                            }
                        });
                    }
                }));
                openApi.setTags(tags);
            }

            // 2. Sort the tags based on min x-order of operations under each tag
            if (openApi.getTags() != null) {
                openApi.getTags().sort(Comparator.comparing(tag -> getTagOrder(tag, openApi)));
            }

            // 3. Sort the paths
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

    private int getTagOrder(io.swagger.v3.oas.models.tags.Tag tag, OpenAPI openApi) {
        return openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(op -> op.getTags() != null && op.getTags().contains(tag.getName()))
                .map(op -> {
                    Map<String, Object> extensions = op.getExtensions();
                    Object order = extensions != null ? extensions.get("x-order") : null;
                    return order != null ? (int) order : 0;
                })
                .min(Integer::compareTo)
                .orElse(0); // default to 0 so 999 goes to the bottom
    }

    private int getOrder(PathItem pathItem) {
        return pathItem.readOperations().stream()
                .map(operation -> {
                    Map<String, Object> extensions = operation.getExtensions();
                    Object order = extensions != null ? extensions.get("x-order") : null;
                    return order != null ? (int) order : 0;
                })
                .min(Integer::compareTo)
                .orElse(0); // default to 0 so 999 goes to the bottom
    }
}
