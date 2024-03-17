package lt.viko.eif.pi21e;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCassandraRepositories
@EnableScheduling
public class CHATRIE {
    private static final Logger logger = LoggerFactory.getLogger(CHATRIE.class);

    public static void main(String... args) {
        SpringApplication.run(CHATRIE.class, args);
        logger.info("Access the Swagger UI here: localhost:8090/swagger-ui.html");
    }
}