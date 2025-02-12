package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "init" })
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@EnableDiscoveryClient
public class ShoppingCartEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartEcommerceApplication.class, args);
	}

}
