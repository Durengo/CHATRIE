package lt.viko.eif.pi21e.WebSocket;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.AllArgsConstructor;
import lt.viko.eif.pi21e.Utility.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class SocketSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SocketSessionManager.class);
    // Using a composite key: "from_to_lobbyId" to store WebSocketSession
    private final Map<String, SocketIOClient> userSessions = new ConcurrentHashMap<>();
    private final RabbitMQConfig rabbitMQConfig;

    public void addUserSession(String lobbyId, String to, String from, SocketIOClient session) {
        String key = getKey(lobbyId, to, from);
        userSessions.put(key, session);
        rabbitMQConfig.createQueue(UUID.fromString(lobbyId), to);
        logger.info("Added WebSocket session for user: {} to {} in lobby: {}", from, to, lobbyId);
    }

    public void removeUserSession(String lobbyId, String to, String from) {
        String key = getKey(lobbyId, to, from);
        userSessions.remove(key);
        rabbitMQConfig.removeQueue(UUID.fromString(lobbyId), to);
        logger.info("Removed WebSocket session for user: {} to {} in lobby: {}", from, to, lobbyId);
    }

    public SocketIOClient getUserSession(String lobbyId, String to, String from) {
        String key = getKey(lobbyId, to, from);
        return userSessions.get(key);
    }

    private String getKey(String lobbyId, String to, String from) {
        return lobbyId + "-" + to + "-" + from;
    }

    @Bean
    public Map<String, SocketIOClient> userSessions() {
        return this.userSessions;
    }

    public void printSessions() {
        for (Map.Entry<String, SocketIOClient> entry : userSessions.entrySet()) {
            String key = entry.getKey();
            SocketIOClient value = entry.getValue();
            logger.info(String.format("Key: " + key + ", Value: " + value));
        }
    }

    // Very buggy. Disabled for now.
//    @Scheduled(fixedDelay = 15000)
    public void checkSocketConnections() {
        for (Map.Entry<String, SocketIOClient> entry : userSessions.entrySet()) {
//            logger.info("Checking this {}", String.format("%s", entry.getKey()));

            String key = entry.getKey();
            SocketIOClient session = entry.getValue();
            if (!session.isChannelOpen()) {
                String[] keys = key.split("_");
                logger.warn("Session is being dropped as it's no longer connected.");
                removeUserSession(keys[0], keys[1], keys[2]);
            }
        }
    }
}
