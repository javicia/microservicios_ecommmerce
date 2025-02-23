package utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MongoDBConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoDBConfig.class);

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoTemplate template = new MongoTemplate(mongoClient, "user_ecommerce");
        checkMongoConnection(template);
        return template;
    }

    private void checkMongoConnection(MongoTemplate mongoTemplate) {
        try {
            mongoTemplate.getDb().listCollectionNames();
            log.info("Conexión a MongoDB establecida correctamente.");
        } catch (Exception e) {
            log.error("No se pudo conectar a MongoDB: {}", e.getMessage());
            throw new IllegalStateException("Error de conexión con MongoDB.", e);
        }
    }
}
