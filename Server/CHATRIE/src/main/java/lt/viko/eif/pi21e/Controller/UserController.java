package lt.viko.eif.pi21e.Controller;

import lt.viko.eif.pi21e.Entities.User;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private static final String consumeProduceType = MediaType.APPLICATION_JSON_VALUE;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(path = "", produces = consumeProduceType)
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @PostMapping(path = "", consumes = consumeProduceType, produces = consumeProduceType)
    public User createUser(@RequestBody User user) throws Exception {
        if(userRepository.findById(user.getNickname()).isPresent())
            throw new Exception("Nickname already taken.");

        return userRepository.save(user);
    }
}
