package dev.abhishek.ecommerce.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Abhishek",
                        email = "aabhiojha08@outlook.com"
                ),
                description = "OpenAPI definition for the ecommerce application.<br><br><b>System Roles:</b><br>- <b>CUSTOMER:</b> Regular user who can browse products, add them to cart, and place orders.<br>- <b>SELLER:</b> User who can add, update, and manage their own products and related images.<br>- <b>ADMIN:</b> Administrator who can manage categories, users, and overall system configuration.",
                title = "Ecommerce api"
        )
)
@SecurityScheme(
        name = OpenApiConfig.BEARER_SCHEME,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI ecommerceOpenApi() {
        return new OpenAPI()
                .schemaRequirement(
                        BEARER_SCHEME,
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );
    }

    @Bean
    public OpenApiCustomizer secureProtectedOperations() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) {
                return;
            }

            paths.forEach((path, pathItem) -> {
                if (pathItem == null) {
                    return;
                }

                for (Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> entry : pathItem.readOperationsMap().entrySet()) {
                    if (isProtected(path, entry.getKey())) {
                        entry.getValue().addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
                    }
                }
            });
        };
    }

    private boolean isProtected(String path, PathItem.HttpMethod method) {
        if (!path.startsWith("/api/")) {
            return false;
        }

        if (path.startsWith("/api/auth/")) {
            return false;
        }

        if (path.equals("/api/payments/checkout/success") || path.equals("/api/payments/checkout/cancel")) {
            return false;
        }

        if (method == PathItem.HttpMethod.GET && (path.startsWith("/api/products") || path.startsWith("/api/categories"))) {
            return false;
        }

        return true;
    }

    @Bean
    public OperationCustomizer customizeOperations() {
        return (operation, handlerMethod) -> {
            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (preAuthorize == null) {
                preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
            }
            
            if (preAuthorize != null) {
                String expression = preAuthorize.value();
                String roles = "";
                if (expression.contains("hasRole")) {
                    roles = expression.replaceAll(".*hasRole\\('([^']+)'\\).*", "$1");
                } else if (expression.contains("hasAnyRole")) {
                    roles = expression.replaceAll(".*hasAnyRole\\(([^)]+)\\).*", "$1").replace("'", "");
                }
                
                if (!roles.isEmpty()) {
                    String existingDescription = operation.getDescription() != null ? operation.getDescription() : "";
                    operation.setDescription(existingDescription + "<br><br><b>Allowed Roles:</b> " + roles);
                }
            }
            return operation;
        };
    }
}
