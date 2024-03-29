package lt.viko.eif.pi21e.Controller;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Entities.LobbyChatKey;
import lt.viko.eif.pi21e.Repositories.ChatRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/chats")
@RequiredArgsConstructor
public class ChatController {
    private static final String consumeProduceType = MediaType.APPLICATION_JSON_VALUE;
    private final ChatRepository chatRepository;

    @GetMapping(produces = consumeProduceType)
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    @GetMapping(path = "/{id}", produces = consumeProduceType)
    public ResponseEntity<List<Chat>> getChatById(@PathVariable UUID id) {
        List<Chat> chats = chatRepository.findByLobbyUUID(id);

        if (!chats.isEmpty()) {
            return ResponseEntity.ok(chats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = consumeProduceType, produces = consumeProduceType)
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        Chat createdChat = chatRepository.save(chat);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

//    @DeleteMapping(path = "/{id}")
//    public ResponseEntity<Void> deleteChat(@PathVariable UUID id) {
//        if (chatRepository.existsById(id)) {
//            chatRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}

