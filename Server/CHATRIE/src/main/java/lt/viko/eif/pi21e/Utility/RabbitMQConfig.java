package lt.viko.eif.pi21e.Utility;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    public String QUEUE_NAME;

    @Value("${rabbitmq.exchange.name}")
    public String EXCHANGE_NAME;

    @Value("${rabbitmq.routing.key}")
    public String ROUTING_KEY;

    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.http.port}")
    private String port;
    public final AmqpAdmin amqpAdmin;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    public RabbitMQConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        return rabbitTemplate;
    }

    public Queue createQueue(UUID lobbyId, String sendToUsername) {
        // TODO: Add logic so that we don't create or replace queues if we provide the same arguments.
        String queueName = QUEUE_NAME + "-" + lobbyId + "-" + sendToUsername;
        Queue queue = new Queue(queueName);

        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(exchange())
                .with(ROUTING_KEY);

        amqpAdmin.declareBinding(binding);

        return queue;
    }

    public void removeQueue(UUID lobbyId, String sendToUsername) {
        String queueName = QUEUE_NAME + "-" + lobbyId + "-" + sendToUsername;
        amqpAdmin.deleteQueue(queueName);
    }

    @Bean
    public Queue autoDeleteUserQueue() {
        return new AnonymousQueue();
    }

    @PostConstruct
    private void purgeQueues() {
        try {
            String fullUrl = String.format("http://%s:%s/api/queues", host, port);
            List<String> queues = getQueueNamesFromAPI(fullUrl);

            if (queues.isEmpty())
                logger.warn("no queues identified.");

            for (var queue : queues) {
                if (!queue.equals(QUEUE_NAME))
                    purgeQueueDirect(queue);
            }
        } catch (Exception e) {
            logger.error("Could not purge queues. (could be empty)");
        }

    }

    public List<String> getQueueNamesFromAPI(String strURL) throws IOException {
        String loginPassword = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(loginPassword.getBytes());

        URL url = new URL(strURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        List<String> queueNames = new ArrayList<>();
        String[] split = response.toString().split("\"name\":\"");
        for (int i = 1; i < split.length; i++) {
            String nameRaw = split[i];
            int index = nameRaw.indexOf("\"");
            if (index > 0 && !nameRaw.startsWith("amq.")) {
                String name = nameRaw.substring(0, index);
                queueNames.add(name);
            }
        }
        return queueNames;
    }

    private void purgeQueueDirect(String fullQueueName) {
        amqpAdmin.deleteQueue(fullQueueName);
    }
}