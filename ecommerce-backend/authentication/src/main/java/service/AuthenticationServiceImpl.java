package service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import model.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Value("${keycloak.auth.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.auth.realm}")
    private String realm;

    @Value("${keycloak.auth.client-id}")
    private String clientId;

    @Value("${keycloak.auth.client-secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();


	@Override
	public ResponseEntity<?> login(LoginRequest loginRequest) {
		 String tokenEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

	        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	        params.add("grant_type", "password");
	        params.add("client_id", clientId);
	        if (clientSecret != null && !clientSecret.isEmpty()) {
	            params.add("client_secret", clientSecret);
	        }
	        params.add("username", loginRequest.getUsername());
	        params.add("password", loginRequest.getPassword());

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

	        try {
	        	// Realiza la petición POST al endpoint de Keycloak
	            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, requestEntity, Map.class);

	            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            	// Devuelve el token (y demás datos) al cliente
	            	logger.info("Autenticación exitosa para el usuario: {}", loginRequest.getUsername());
	                return ResponseEntity.ok(response.getBody());
	            } else {
	                logger.error("Error en la autenticación. Status: {}", response.getStatusCode());
	                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
	            }
	        } catch (Exception e) {
	            logger.error("Error obteniendo token de Keycloak", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error obteniendo token: " + e.getMessage());
	        }
	}

}
