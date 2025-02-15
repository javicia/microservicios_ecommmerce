package configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class GatewaySwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("API Gateway - Documentación Centralizada").version("1.0")
						.description("Esta API centraliza la documentación de todos los microservicios")
						.contact(new Contact().name("Soporte API").email("javigpdeveloper@gmail.com")))
				.servers(List.of(new Server().url("http://localhost:7000").description("API Gateway")

				));
	}
}
