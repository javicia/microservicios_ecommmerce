package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableMongoRepositories(basePackages = "repository")
@SpringBootApplication(scanBasePackages = {"init", "controller", "model", "service", "configuration", "security", "utils"})
@EnableDiscoveryClient 
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(UserApplication.class);
        app.setLogStartupInfo(true);
        app.run(args);
    }

    @Bean
	@LoadBalanced
	public RestTemplate template() {
		return new RestTemplate();
	}
    
   
}
