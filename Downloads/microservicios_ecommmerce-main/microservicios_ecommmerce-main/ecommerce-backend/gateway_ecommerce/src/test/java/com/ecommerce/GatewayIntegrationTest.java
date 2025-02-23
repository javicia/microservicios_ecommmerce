package com.ecommerce;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayIntegrationTest {
	
	@Autowired
    private WebTestClient webTestClient;

	@Test
    public void testDirectRouteToUserEcommerce() {
        webTestClient
            .mutate()
            .defaultHeaders(headers -> headers.setBasicAuth("admin", "admin"))
            .build()
            .get()
            .uri("/api/users/ping")
            .exchange()
            .expectStatus().isCreated();  // Se espera 201 CREATED
    }
}


