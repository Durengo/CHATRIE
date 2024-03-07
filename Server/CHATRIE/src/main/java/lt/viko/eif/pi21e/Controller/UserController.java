package lt.viko.eif.pi21e.Controller;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.User;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private static final String consumeProduceType = MediaType.APPLICATION_JSON_VALUE;
    private final UserRepository userRepository;

    @GetMapping(produces = consumeProduceType)
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/{username}", produces = consumeProduceType)
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findById(username);
        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = consumeProduceType, produces = consumeProduceType)
    public ResponseEntity<User> createUser(@RequestBody User user) throws Exception {
        if (userRepository.existsById(user.getUsername())) {
            throw new Exception("Username already taken.");
        }
        User createdUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(path = "/{username}", consumes = consumeProduceType, produces = consumeProduceType)
    public ResponseEntity<String> updateUserPassword(
            @PathVariable String username,
            @RequestParam("newPassword") String newPassword
    ) {
        Optional<User> userOptional = userRepository.findById(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            try {
                user.changePassword(newPassword, user.getPassword());
                userRepository.save(user);
                return ResponseEntity.ok("Password changed successfully for user with nickname: " + username);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to change password: " + e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        if (userRepository.existsById(username)) {
            userRepository.deleteById(username);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

