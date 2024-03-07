package lt.viko.eif.pi21e.Controller;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Lobby;
import lt.viko.eif.pi21e.Repositories.LobbyRepository;
import lt.viko.eif.pi21e.Services.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/lobbies")
@RequiredArgsConstructor
public class LobbyController {
    private final LobbyRepository lobbyRepository;
    private final LobbyService lobbyService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    @GetMapping("/users")
    public ResponseEntity<Lobby> findLobbyByUsers(
            @RequestParam("user1Nickname") String user1Nickname,
            @RequestParam("user2Nickname") String user2Nickname
    ) {
        Lobby lobby = lobbyService.findSpecific(user1Nickname, user2Nickname);
        if (lobby != null) {
            return ResponseEntity.ok(lobby);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lobby> getLobbyById(@PathVariable UUID lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        return lobbyOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lobby> createLobby(@RequestBody Lobby lobby) {
        Lobby savedLobby = lobbyRepository.save(lobby);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLobby);
    }

    @DeleteMapping(path = "/{lobbyId}")
    public ResponseEntity<Void> deleteLobby(@PathVariable UUID lobbyId) {
        if (lobbyRepository.existsById(lobbyId)) {
            lobbyRepository.deleteById(lobbyId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/user/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Lobby> getLobbiesByUser(@PathVariable String nickname) {
        return lobbyService.findAllByUser(nickname);
    }
}

