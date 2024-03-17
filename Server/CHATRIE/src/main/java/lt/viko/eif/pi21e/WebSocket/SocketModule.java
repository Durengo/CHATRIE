package lt.viko.eif.pi21e.WebSocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Publisher.RabbitMQProducer;
import lt.viko.eif.pi21e.Utility.RabbitMQConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class SocketModule {

    private final SocketIOServer server;
    private final RabbitMQProducer rabbitMQProducer;
    private final SocketSessionManager sessionManager;
    private final RabbitMQConfig rabbitMQConfig;

    public SocketModule(SocketIOServer server, RabbitMQProducer rabbitMQProducer, SocketSessionManager socketSessionManager, RabbitMQConfig rabbitMQConfig) {
        this.server = server;
        this.rabbitMQProducer = rabbitMQProducer;
        this.sessionManager = socketSessionManager;
        this.rabbitMQConfig = rabbitMQConfig;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("send_chat", Chat.class, onChatReceived());
    }

    private DataListener<Chat> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            String queue = String.format("%s-%s-%s", rabbitMQConfig.QUEUE_NAME, data.getLobbyChatKey().getLobbyId(), data.getSentTo());
            log.info("Sending to following queue: {}", queue);
            // Extract lobbyId and userNickname from data
            UUID lobbyId = data.getLobbyChatKey().getLobbyId();
            String sendTo = data.getSentTo();
            // Send the chat message to RabbitMQ
            rabbitMQProducer.sendChatMessage(data, lobbyId, sendTo);
        };
    }

    private ConnectListener onConnected() {
        return (client) -> {
            Map<String, List<String>> queryParams = client.getHandshakeData().getUrlParams();
            String room = getQueryParam(queryParams, "room");
            String from = getQueryParam(queryParams, "from");
            String to = getQueryParam(queryParams, "to");

            if (
                    (room == null || from == null || to == null)
                            || ((room.isEmpty() || room.isBlank()) || (from.isEmpty() || from.isBlank()) || (to.isEmpty() || to.isBlank()))
                            || ("[object Object]".equals(room) || "[object Object]".equals(from) || "[object Object]".equals(to))
            ) {
                log.warn("Socket not properly formatted. Dropping connection.");
                client.disconnect();
                return;
            }

            if (sessionManager.getUserSession(room, to, from) == null) {
                client.joinRoom(room);
                sessionManager.addUserSession(room, to, from, client);
                log.info(String.format("Socket ID[%s] Connected to socket.\nLobby [%s] between [%s] and [%s]", client.getSessionId().toString(), room, from, to));
            } else {
                log.warn(String.format("Socket ID[%s] already connected to socket. Ignoring duplicate connection.", client.getSessionId().toString()));
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            String from = client.getHandshakeData().getSingleUrlParam("from");
            String to = client.getHandshakeData().getSingleUrlParam("to");

            if (sessionManager.getUserSession(room, to, from) != null) {
                sessionManager.removeUserSession(room, to, from);
                log.info("Client[{}] - Disconnected from socket", client.getSessionId().toString());
            } else {
                log.warn(String.format("Socket ID[%s] already disconnected from socket. Ignoring duplicate disconnection.", client.getSessionId().toString()));
            }
        };
    }

    private String getQueryParam(Map<String, List<String>> queryParams, String key) {
        List<String> values = queryParams.get(key);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

}
