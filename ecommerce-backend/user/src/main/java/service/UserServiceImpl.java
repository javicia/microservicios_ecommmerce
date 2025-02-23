package service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import configuration.KeycloakAdminClient;
import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;
import utils.Constants;
import utils.Validation;


@Service
@Primary
public class UserServiceImpl implements IUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KeycloakAdminClient keycloakAdminClient; // Cliente que invoca la API de Keycloak
    private final ObjectMapper objectMapper;
    private final Validation validation;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           KafkaTemplate<String, String> kafkaTemplate, KeycloakAdminClient keycloakAdminClient, ObjectMapper objectMapper,
                           Validation validation) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.keycloakAdminClient = keycloakAdminClient;
        this.objectMapper = objectMapper;
        this.validation = validation;
    }

    @Override
    public User registerUser(User user) {
        // Validar los datos del usuario para el registro
        validation.validateUserForRegistration(user);
        
        // Guarda la contraseña en texto plano en una variable temporal
        String plainPassword = user.getPassword();
        
        // Encripta la contraseña para almacenarla en la base de datos
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Guarda el usuario en la base de datos (se guarda la versión encriptada)
        User savedUser = userRepository.save(user);
        
        // Antes de crear el usuario en Keycloak, restauramos la contraseña en texto plano
        savedUser.setPassword(plainPassword);
        
        // Llamada a Keycloak para crear el usuario
        try {
            keycloakAdminClient.createKeycloakUser(savedUser);
            logger.info("Usuario creado en Keycloak: {}", savedUser.getUsername());
        } catch (Exception e) {
            logger.error("Error al crear usuario en Keycloak. Detalles: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al crear usuario en Keycloak", e);
        }
        
        // Publicar evento de creación de usuario (en Kafka, por ejemplo)
        publishEvent("USER_CREATED", savedUser);
        
        logger.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());
        return savedUser;
    }


    @Override
    public User updateUser(String id, User updatedUser) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío.");
        }
        // Buscar el usuario existente en la base de datos
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el ID: " + id));
        
        // Variable para almacenar la nueva contraseña en texto plano (si se actualiza)
        String plainPassword = null;
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            plainPassword = updatedUser.getPassword();
            // Encripta la nueva contraseña para almacenarla en la base de datos
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        // Actualiza los campos del usuario existente con los datos nuevos
        validation.updateExistingUser(existingUser, updatedUser);
        
        // Guarda el usuario actualizado en la base de datos (la contraseña ya estará encriptada si fue actualizada)
        User savedUser = userRepository.save(existingUser);
        
        // Si se actualizó la contraseña, restauramos el valor en texto plano para enviarlo a Keycloak
        if (plainPassword != null) {
            savedUser.setPassword(plainPassword);
        }
        
        // Llamada a Keycloak para actualizar el usuario
        try {
            keycloakAdminClient.updateKeycloakUser(savedUser);
            logger.info("Usuario actualizado en Keycloak: {}", savedUser.getUsername());
        } catch (Exception e) {
            logger.error("Error al actualizar usuario en Keycloak. Detalles: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al actualizar usuario en Keycloak", e);
        }
        
        // Publicar evento de actualización de usuario
        publishEvent("USER_UPDATED", savedUser);
        
        logger.info("Usuario actualizado exitosamente con ID: {}", savedUser.getId());
        return savedUser;
    }


    @Override
    public void deleteUser(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío.");
        }
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el ID: " + id));

        // Elimina el usuario de la base de datos
        userRepository.deleteById(id);
        
        // Eliminar en Keycloak
        try {
            keycloakAdminClient.deleteKeycloakUser(existingUser.getUsername());
            logger.info("Usuario eliminado en Keycloak: {}", existingUser.getUsername());
        } catch (Exception e) {
            logger.error("Error al eliminar usuario en Keycloak. Detalles: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al eliminar usuario en Keycloak", e);
        }
        
     // Publicar evento de eliminación de usuario antes de eliminarlo
        publishEvent("USER_DELETED", existingUser);


        logger.info("Usuario eliminado exitosamente con ID: {}", id);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el nombre: " + username));
    }

    @Override
    public User getUserByEmail(String email) {
        if (!validation.isValidEmail(email)) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido.");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    @Override
    public User getUserByDNI(String dni) {
        if (!validation.isValidDni(dni)) {
            throw new IllegalArgumentException("El formato del DNI no es válido.");
        }
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el DNI: " + dni));
    }

    @Override
    public List<User> getUsersByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía.");
        }

        return userRepository.findByCity(city);
    }

    @Override
    public List<User> getUsersByZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El código postal no puede estar vacío.");
        }

        return userRepository.findByZipCode(zipCode);
    }

    /**
     * Publica un evento en Kafka.
     *
     * @param eventType El tipo de evento (e.g., USER_CREATED, USER_UPDATED, USER_DELETED).
     * @param user      El usuario relacionado con el evento.
     */
    private void publishEvent(String eventType, User user) {
        String userJson = convertirAJson(user);
        if (!userJson.isEmpty()) {
        	// Combina el tipo de evento y el objeto JSON en un solo string
            String combinedMessage = eventType + ":" + userJson;
         // Envía TODO en el value (sin usar la key para separar)
            kafkaTemplate.send(Constants.USER_TOPIC, combinedMessage);
            logger.debug("Evento {} publicado para el usuario: {}", eventType, user.getId());
        } else {
            logger.error("No se pudo convertir el usuario a JSON para el evento: {}", eventType);
        }
    }


    /**
     * Convierte un objeto User a una cadena JSON.
     *
     * @param user El usuario a convertir.
     * @return La representación JSON del usuario, o una cadena vacía si falla la conversión.
     */
    private String convertirAJson(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el usuario a JSON: {}", e.getMessage());
            return "";
        }
    }
}
