package init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = { "init", "configuration" })
@EnableDiscoveryClient
public class GatewayEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayEcommerceApplication.class, args);
	}

}
