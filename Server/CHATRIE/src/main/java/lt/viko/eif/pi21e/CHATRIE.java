package lt.viko.eif.pi21e;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories
public class CHATRIE {
    public static void main(String... args) {
        SpringApplication.run(CHATRIE.class, args);
    }
}