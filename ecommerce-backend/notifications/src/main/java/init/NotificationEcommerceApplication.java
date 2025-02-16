package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "init", "service", "controller", "configuration", "security", "exception",
		"dto" })
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@EnableDiscoveryClient
public class NotificationEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationEcommerceApplication.class, args);
	}

}
