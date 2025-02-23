package configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.User;
import repository.UserRepository;

@Component
public class UserEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);
    
    @Autowired
    private UserRepository userRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "user-events", groupId = "authentication_group")
    public void handleUserEvents(String message) {
        try {
            // Mensaje tiene el formato "EVENT_TYPE:json"
            String[] parts = message.split(":", 2);
            if (parts.length != 2) {
                logger.error("Mensaje mal formado: {}", message);
                return;
            }
            String eventType = parts[0];
            String userJson = parts[1];

            User user = objectMapper.readValue(userJson, User.class);

            switch (eventType) {
                case "USER_CREATED":
                    userRepository.save(user);
                    logger.info("Usuario creado: {}", user.getUsername());
                    break;
                case "USER_UPDATED":
                    userRepository.save(user);
                    logger.info("Usuario actualizado: {}", user.getUsername());
                    break;
                case "USER_DELETE":
                    userRepository.delete(user);
                    logger.info("Usuario eliminado: {}", user.getUsername());
                    break;
                default:
                    logger.warn("Evento desconocido: {}", eventType);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error al manejar el evento de usuario: {}", e.getMessage(), e);
        }
    }
}
