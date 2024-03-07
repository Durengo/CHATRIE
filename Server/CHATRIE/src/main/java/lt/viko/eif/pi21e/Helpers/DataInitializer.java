package lt.viko.eif.pi21e.Helpers;

import lombok.AllArgsConstructor;
import lt.viko.eif.pi21e.Authentication.AuthenticationResponse;
import lt.viko.eif.pi21e.CHATRIE;
import lt.viko.eif.pi21e.Entities.*;
import lt.viko.eif.pi21e.Repositories.ChatRepository;
import lt.viko.eif.pi21e.Repositories.LobbyRepository;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import lt.viko.eif.pi21e.Utility.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final ChatRepository chatRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {

        // Create users
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        User user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER)
                .build();

        userRepository.save(user2);

        // Create a lobby
        Lobby lobby;
        if (lobbyRepository.findAll().isEmpty()) {
            UUID uuid = UUID.randomUUID();

            lobby = Lobby.builder()
                    .lobbyId(uuid)
                    .user1Nickname(user.getUsername())
                    .user2Nickname(user2.getUsername())
                    .build();

            logger.info("New lobby created: " + lobby.toString());

            lobbyRepository.save(lobby);
        } else {
            lobby = lobbyRepository.findAll().getFirst();
        }

        // Create some chats
        if (chatRepository.findAll().isEmpty()) {
            var today = new Date();

            long millisecondsToAdd = 10 * 1000;
            LobbyChatKey lobbyChatKey1 = new LobbyChatKey(lobby.getLobbyId(), new Date(today.getTime() + millisecondsToAdd));
            Chat chat1 = Chat.builder()
                    .lobbyChatKey(lobbyChatKey1)
                    .sentBy(user.getUsername())
                    .sentTo(user2.getUsername())
                    .message("Hi!")
                    .build();

            millisecondsToAdd += 5;
            LobbyChatKey lobbyChatKey2 = new LobbyChatKey(lobby.getLobbyId(), new Date(today.getTime() + millisecondsToAdd));
            Chat chat2 = Chat.builder()
                    .lobbyChatKey(lobbyChatKey2)
                    .sentBy(user2.getUsername())
                    .sentTo(user.getUsername())
                    .message("Hello!")
                    .build();

            millisecondsToAdd += 5;
            LobbyChatKey lobbyChatKey3 = new LobbyChatKey(lobby.getLobbyId(), new Date(today.getTime() + millisecondsToAdd));
            Chat chat3 = Chat.builder()
                    .lobbyChatKey(lobbyChatKey3)
                    .sentBy(user.getUsername())
                    .sentTo(user2.getUsername())
                    .message("This is great!")
                    .build();

            millisecondsToAdd += 5;
            LobbyChatKey lobbyChatKey4 = new LobbyChatKey(lobby.getLobbyId(), new Date(today.getTime() + millisecondsToAdd));
            Chat chat4 = Chat.builder()
                    .lobbyChatKey(lobbyChatKey4)
                    .sentBy(user2.getUsername())
                    .sentTo(user.getUsername())
                    .message("I know!")
                    .build();

            chatRepository.save(chat1);
            chatRepository.save(chat2);
            chatRepository.save(chat3);
            chatRepository.save(chat4);

            // TODO: Test edge-case when a user simultaneously sends 2 messages or both users send a message at the same time.
        }
    }
}
