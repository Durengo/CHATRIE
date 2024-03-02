package lt.viko.eif.pi21e.Services;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Lobby;
import lt.viko.eif.pi21e.Repositories.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final LobbyRepository lobbyRepository;

    public Lobby createLobby(String user1, String user2) {
        Lobby lobby = new Lobby(UUID.randomUUID(), user1, user2);

        return lobbyRepository.save(lobby);
    }
}
