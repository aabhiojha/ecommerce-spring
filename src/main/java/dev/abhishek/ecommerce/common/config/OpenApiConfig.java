package dev.abhishek.ecommerce.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Abhishek",
                        email = "aabhiojha08@outlook.com",
                        url = "abhishekojha.com.np"
                ),
                description = "OpenAPI definition for the ecommerce application.",
                title = "Ecommerce api"
        ),
        servers = {
                @Server(
                        description = "Local env",
                        url = "http://localhost:8080"
                )
        }
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
}
