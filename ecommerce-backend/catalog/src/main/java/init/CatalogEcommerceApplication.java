package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"init", "controller", "service","configuration", "security","exception"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@EnableDiscoveryClient 
public class CatalogEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogEcommerceApplication.class, args);
	}
	
	 @Bean
		@LoadBalanced
		public RestTemplate template() {
			return new RestTemplate();
		}

}
