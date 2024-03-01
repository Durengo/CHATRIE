package lt.viko.eif.pi21e.Utility;

import lt.viko.eif.pi21e.Entities.Lobby;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/*
 * We define a fanout exchange (`lobby-exchange`) for broadcasting to multiple queues.
 * Queues are dynamically named based on the lobby ID which is a UUID (e.g., 'lobby-queue-aacf32e3-9195-4e36-b5de-6d7597013999').
 * Bindings connect the queues to the exchange.
 */

@Configuration
public class RabbitMQConfig {
    static final String LOBBY_EXCHANGE_NAME = "lobby-exchange";

    /**
     * Creates a group of AMQP declarations (queues and bindings) for the lobby system.
     * A separate queue is dynamically generated for each lobby based on its ID, and all queues are bound to the fanout exchange.
     *
     * @param lobbies A list of existing Lobby entities.
     * @return A Declarables object containing the queue and binding definitions.
     */
    @Bean
    public Declarables lobbyBindings(List<Lobby> lobbies) {
        List<Declarable> bindings = lobbies.stream()
                .map(lobby -> new Queue("lobby-queue-" + lobby.getLobbyId(), false))
                .map(queue -> BindingBuilder.bind(queue).to(lobbyExchange()))
                .collect(Collectors.toList());
        bindings.add(lobbyExchange()); // Ensure the exchange itself is declared
        return new Declarables(bindings);
    }

    /**
     * Creates the fanout exchange used for broadcasting chat messages to lobby queues.
     *
     * @return A FanoutExchange instance with the configured exchange name.
     */
    @Bean
    FanoutExchange lobbyExchange() {
        return new FanoutExchange(LOBBY_EXCHANGE_NAME);
    }

    /**
     * Provides the Jackson-based message converter for serializing/deserializing messages to/from JSON when using RabbitMQ.
     *
     * @return A Jackson2JsonMessageConverter instance.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Constructs the RabbitTemplate, a core Spring AMQP utility for sending and receiving messages. The configured message converter is applied.
     *
     * @param connectionFactory The connection factory for accessing the RabbitMQ broker.
     * @return A fully configured RabbitTemplate instance.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}