package configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	// swagger-ui: http://localhost:8080/swagger-ui/index.html
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("API de pedidos").version("1.0")
				.description("Documentación de la API para el microservicio de pedidos"));
	}

}
