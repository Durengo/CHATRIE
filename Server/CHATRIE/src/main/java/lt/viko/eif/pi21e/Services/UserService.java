package lt.viko.eif.pi21e.Services;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.User;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Boolean checkForDuplicate(String nickname) {
        var user = userRepository.findByUsername(nickname);

        return user.isPresent();
    }

    public List<String> getAllusers() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
