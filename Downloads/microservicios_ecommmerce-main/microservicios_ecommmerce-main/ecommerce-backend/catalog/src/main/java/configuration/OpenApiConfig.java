package configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {
	
	//swagger-ui: http://localhost:8080/swagger-ui/index.html
	 @Bean
	    public OpenAPI customOpenAPI() {
	        return new OpenAPI()
	                .info(new Info()
	                    .title("API de catálogo de productos")
	                    .version("1.0")
	                    .description("Documentación de la API para el microservicio de catálogo de productos"));
	    }

}
