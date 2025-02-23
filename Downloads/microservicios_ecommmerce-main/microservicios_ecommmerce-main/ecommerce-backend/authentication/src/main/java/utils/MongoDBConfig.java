package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

@Configuration
public class MongoDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBConfig.class);

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoTemplate template = new MongoTemplate(mongoClient, "user_ecommerce");
        checkMongoConnection(template);
        return template;
    }

    private void checkMongoConnection(MongoTemplate mongoTemplate) {
        try {
            mongoTemplate.getDb().listCollectionNames();
            logger.info("Conexión a MongoDB establecida correctamente.");
        } catch (Exception e) {
            logger.error("No se pudo conectar a MongoDB: {}", e.getMessage(), e);
            throw new IllegalStateException("Error de conexión con MongoDB.", e);
        }
    }
}
