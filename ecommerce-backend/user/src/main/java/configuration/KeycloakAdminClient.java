package configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import model.User;

@Component
public class KeycloakAdminClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAdminClient.class);

    @Value("${keycloak.admin.realm}")
    private String realm; // UsersRealm

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl; // http://localhost:8080

    @Value("${keycloak.admin.client-id}")
    private String adminClientId; // admin-service

    @Value("${keycloak.admin.client-secret}")
    private String adminClientSecret; // client-secret

    private final RestTemplate restTemplate = new RestTemplate();
    private String adminToken;
    private long tokenExpiryTime; // Timestamp en milisegundos

    @PostConstruct
    public void init() {
        // Obtener un token de administrador al iniciar la app
        this.adminToken = getAdminAccessToken();
    }

    private synchronized String getAdminAccessToken() {
        // Verificar si el token actual es válido
        if (adminToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return adminToken;
        }
        //

        try {
            String tokenEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "client_credentials");
            params.add("client_id", adminClientId);
            params.add("client_secret", adminClientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            logger.debug("Solicitando token a Keycloak con client_id: {}", adminClientId);

            ResponseEntity<Map> response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Token de administrador obtenido exitosamente.");
                this.adminToken = (String) response.getBody().get("access_token");
                Integer expiresIn = (Integer) response.getBody().get("expires_in");
                this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000) - (60 * 1000); // Renovar 1 minuto antes
                return adminToken;
            } else {
                logger.error("No se pudo obtener el token de admin de Keycloak. Status: {}", response.getStatusCode());
                throw new RuntimeException("No se pudo obtener el token de admin de Keycloak");
            }
        } catch (Exception e) {
            logger.error("Error al obtener token de admin", e);
            throw new RuntimeException("Error al obtener token de admin", e);
        }
    }

    private String findKeycloakUserIdByUsername(String username) {
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    List.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Error buscando userID en Keycloak: {}", response.getStatusCode());
                throw new RuntimeException("Error buscando userID en Keycloak: " + response.getStatusCode());
            }

            List<Map<String, Object>> users = response.getBody();

            if (users.isEmpty()) {
                logger.error("No se encontró usuario en Keycloak con username={}", username);
                throw new RuntimeException("No se encontró usuario en Keycloak con username=" + username);
            }

            return (String) users.get(0).get("id");
        } catch (HttpClientErrorException e) {
            logger.error("Error al buscar userID en Keycloak: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error al buscar userID en Keycloak: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al buscar userID en Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar userID en Keycloak", e);
        }
    }

    public void createKeycloakUser(User user) {
        // Asegurar que el token es válido
        String token = getAdminAccessToken();

        // Construir JSON con los datos del usuario
        Map<String, Object> body = new HashMap<>();
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());
        body.put("firstName", user.getName());
        body.put("lastName", user.getSurname());
        body.put("emailVerified", true);
        body.put("enabled", true);
        body.put("requiredActions", Collections.emptyList());

        // Atributos personalizados
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("dni", Collections.singletonList(user.getDni()));
        attributes.put("address", Collections.singletonList(user.getAddress()));
        attributes.put("city", Collections.singletonList(user.getCity()));
        //attributes.put("zipcode", Collections.singletonList(user.getZipCode()));

        body.put("attributes", attributes);

        // Asignar contraseña
        Map<String, Object> cred = new HashMap<>();
        cred.put("type", "password");
        cred.put("value", user.getPassword()); // Asegúrate de que es la contraseña en texto plano
        cred.put("temporary", false);
        body.put("credentials", Collections.singletonList(cred));

        // Convertir el cuerpo a JSON
        String userJson;
        try {
            userJson = new ObjectMapper().writeValueAsString(body);
            logger.debug("JSON enviado a Keycloak: {}", userJson);
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el cuerpo a JSON: {}", e.getMessage());
            throw new RuntimeException("Error al convertir el cuerpo a JSON", e);
        }

        // POST a /admin/realms/{realm}/users
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Usa el token válido
        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.debug("Enviando solicitud POST a Keycloak para crear usuario: {}", user.getUsername());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(userJson, headers),
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Error al crear usuario en Keycloak. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error al crear usuario en Keycloak. Status: " + response.getStatusCode());
            } else {
                logger.info("Usuario creado en Keycloak: {}", user.getUsername());
            }
        } catch (HttpClientErrorException.BadRequest e) {
            logger.error("400 Bad Request al intentar crear usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("400 Bad Request al intentar crear usuario en Keycloak.", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("401 Unauthorized al intentar crear usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("401 Unauthorized al intentar crear usuario en Keycloak.", e);
        } catch (Exception e) {
            logger.error("Error al crear usuario en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al crear usuario en Keycloak.", e);
        }
    }

    public void updateKeycloakUser(User user) {
        // Obtener el ID del usuario en Keycloak
        String userId = findKeycloakUserIdByUsername(user.getUsername());

        // Construir el cuerpo de la solicitud con los datos actualizados
        Map<String, Object> body = new HashMap<>();
        body.put("email", user.getEmail());
        body.put("firstName", user.getName());
        body.put("lastName", user.getSurname());
        body.put("enabled", true);

        // Atributos personalizados
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("dni", Collections.singletonList(user.getDni()));
        attributes.put("address", Collections.singletonList(user.getAddress()));
        attributes.put("city", Collections.singletonList(user.getCity()));
        attributes.put("zipcode", Collections.singletonList(user.getZipCode()));
        body.put("attributes", attributes);

        // Actualizar la contraseña si se proporciona
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            Map<String, Object> cred = new HashMap<>();
            cred.put("type", "password");
            cred.put("value", user.getPassword()); // Asegúrate de que es la contraseña en texto plano
            cred.put("temporary", false);
            body.put("credentials", Collections.singletonList(cred));
        }

        // Convertir el cuerpo a JSON
        String userJson;
        try {
            userJson = new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el cuerpo a JSON: {}", e.getMessage());
            throw new RuntimeException("Error al convertir el cuerpo a JSON", e);
        }

        // Construir la URL para actualizar al usuario
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.debug("Enviando solicitud PUT a Keycloak para actualizar usuario: {}", user.getUsername());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(userJson, headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Error al actualizar usuario en Keycloak. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error al actualizar usuario en Keycloak. Status: " + response.getStatusCode());
            } else {
                logger.info("Usuario actualizado en Keycloak: {}", user.getUsername());
            }
        } catch (HttpClientErrorException.BadRequest e) {
            logger.error("400 Bad Request al intentar actualizar usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("400 Bad Request al intentar actualizar usuario en Keycloak.", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("401 Unauthorized al intentar actualizar usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("401 Unauthorized al intentar actualizar usuario en Keycloak.", e);
        } catch (Exception e) {
            logger.error("Error al actualizar usuario en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar usuario en Keycloak.", e);
        }
    }

    public void deleteKeycloakUser(String username) {
        // Obtener el ID del usuario en Keycloak
        String userId = findKeycloakUserIdByUsername(username);

        // Construir la URL para eliminar al usuario
        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminAccessToken());

        logger.debug("Enviando solicitud DELETE a Keycloak para eliminar usuario: {}", username);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Error al eliminar usuario en Keycloak. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error al eliminar usuario en Keycloak. Status: " + response.getStatusCode());
            } else {
                logger.info("Usuario eliminado en Keycloak: {}", username);
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("404 Not Found al intentar eliminar usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("404 Not Found al intentar eliminar usuario en Keycloak.", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("401 Unauthorized al intentar eliminar usuario en Keycloak. Detalles: {}", e.getResponseBodyAsString());
            throw new RuntimeException("401 Unauthorized al intentar eliminar usuario en Keycloak.", e);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario en Keycloak.", e);
        }
    }
}
