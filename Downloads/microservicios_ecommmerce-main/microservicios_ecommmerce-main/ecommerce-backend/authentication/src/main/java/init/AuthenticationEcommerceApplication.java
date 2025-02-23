package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "repository")
@SpringBootApplication(scanBasePackages = {"init", "controller", "model", "security", "service"})
@EnableDiscoveryClient 
public class AuthenticationEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationEcommerceApplication.class, args);
	}
	

}
