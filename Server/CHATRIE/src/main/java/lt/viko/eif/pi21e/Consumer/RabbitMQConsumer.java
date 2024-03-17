package lt.viko.eif.pi21e.Consumer;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.AllArgsConstructor;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Utility.RabbitMQConfig;
//import lt.viko.eif.pi21e.WebSocket.SocketService;
import lt.viko.eif.pi21e.WebSocket.SocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final SocketSessionManager sessionManager;
//    private final SocketService socketService;
    private final RabbitMQConfig rabbitMQConfig;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeChatMessage(Chat chat) {
        String queue = String.format("%s-%s-%s", rabbitMQConfig.QUEUE_NAME, chat.getLobbyChatKey().getLobbyId(), chat.getSentBy());
        String purgeQueue = String.format("%s-%s-%s", rabbitMQConfig.QUEUE_NAME, chat.getLobbyChatKey().getLobbyId(), chat.getSentTo());
        logger.info("Received chat message from following queue: {}", queue);

        sessionManager.printSessions();

        SocketIOClient userSession = sessionManager.getUserSession(chat.getLobbyChatKey().getLobbyId().toString(), chat.getSentBy(), chat.getSentTo());
        if (userSession != null && userSession.isChannelOpen()) {
            try {
                // Send the chat message to the WebSocket session
                String sendBackQueue = String.format("chat-message-%s-%s", chat.getLobbyChatKey().getLobbyId().toString(), chat.getSentTo());

                logger.info("Sending chat back to: {}", sendBackQueue);
                userSession.sendEvent(sendBackQueue, chat);
            } catch (Exception e) {
                logger.error("Error sending message to WebSocket.", e);
            }
        } else {
            logger.warn("User socket session not found or closed.");
        }

        purgeQueue(purgeQueue);
    }

    private void purgeQueue(String queueName) {
        rabbitMQConfig.amqpAdmin.purgeQueue(queueName, false);
    }

}
