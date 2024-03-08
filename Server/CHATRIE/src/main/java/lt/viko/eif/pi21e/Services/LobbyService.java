package lt.viko.eif.pi21e.Services;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Entities.Lobby;
import lt.viko.eif.pi21e.Repositories.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final LobbyRepository lobbyRepository;

    public Lobby createLobby(String user1, String user2) {
        Lobby lobby = new Lobby(UUID.randomUUID(), user1, user2);

        return lobbyRepository.save(lobby);
    }

    public Lobby findSpecific(String username1, String username2) {
        Optional<Lobby> lobby = findLobby(username1, username2);
        if (lobby.isPresent()) {
            return lobby.get();
        } else {
            // If the lobby is not found with the original order, try the reversed order
            lobby = findLobby(username2, username1);
            return lobby.orElse(null);
        }
    }

    private Optional<Lobby> findLobby(String user1, String user2) {
        List<Lobby> user1Lobbies = lobbyRepository.findByUser1Nickname(user1);
        List<Lobby> user2Lobbies = lobbyRepository.findByUser2Nickname(user1);

        List<Lobby> lobbies = Stream.concat(user1Lobbies.stream(), user2Lobbies.stream())
                .collect(Collectors.toList());

        // Find the lobby where the other user is user2
        return lobbies.stream()
                .filter(lobby -> user2.equals(lobby.getUser1Nickname()) || user2.equals(lobby.getUser2Nickname()))
                .findFirst();
    }

    public List<Lobby> findAllByUser(String username) {
        List<Lobby> user1Lobbies = lobbyRepository.findByUser1Nickname(username);
        List<Lobby> user2Lobbies = lobbyRepository.findByUser2Nickname(username);

        Set<Lobby> uniqueLobbies = new HashSet<>(user1Lobbies);
        uniqueLobbies.addAll(user2Lobbies);

        return new ArrayList<>(uniqueLobbies);
    }

    public List<Lobby> findAllByUsers(String username1, String username2) {
        List<Lobby> user1Lobbies = lobbyRepository.findByUser1Nickname(username1);
        List<Lobby> user2Lobbies = lobbyRepository.findByUser2Nickname(username1);
        List<Lobby> user3Lobbies = lobbyRepository.findByUser1Nickname(username2);
        List<Lobby> user4Lobbies = lobbyRepository.findByUser2Nickname(username2);

        Set<Lobby> uniqueLobbies = new HashSet<>(user1Lobbies);
        uniqueLobbies.addAll(user2Lobbies);
        uniqueLobbies.addAll(user3Lobbies);
        uniqueLobbies.addAll(user4Lobbies);

        return new ArrayList<>(uniqueLobbies);
    }
}
