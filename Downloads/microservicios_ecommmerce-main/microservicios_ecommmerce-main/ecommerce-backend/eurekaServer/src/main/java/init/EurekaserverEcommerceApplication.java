package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(scanBasePackages = {"init", "security"})
@EnableEurekaServer
public class EurekaserverEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverEcommerceApplication.class, args);
	}

}
