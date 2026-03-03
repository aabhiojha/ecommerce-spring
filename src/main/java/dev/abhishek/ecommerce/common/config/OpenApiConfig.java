package dev.abhishek.ecommerce.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Abhishek",
//                    email = ""
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
                ,
                @Server(
                        description = "Production env",
                        url = "http://abhishekojha.com.np/ecommerce-api"
                )
        }
)
public class OpenApiConfig {


}
