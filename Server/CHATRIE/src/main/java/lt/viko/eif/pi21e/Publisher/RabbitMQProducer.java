package lt.viko.eif.pi21e.Publisher;

import lombok.AllArgsConstructor;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Utility.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange exchange;
    private final RabbitMQConfig rabbitMQConfig;

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    public void sendChatMessage(Chat chat, UUID lobbyId, String sendTo) {
        String routingKey = "lobby-queue-" + lobbyId.toString() + "-" + sendTo;
        routingKey = rabbitMQConfig.ROUTING_KEY;

        logger.info(String.format("EXCHANGE: %s | ROUTING: %s", exchange.getName(), routingKey));
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, chat);
    }
}
