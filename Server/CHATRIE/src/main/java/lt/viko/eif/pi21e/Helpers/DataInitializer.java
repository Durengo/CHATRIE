package lt.viko.eif.pi21e.Helpers;

import lombok.AllArgsConstructor;
import lt.viko.eif.pi21e.Authentication.AuthenticationResponse;
import lt.viko.eif.pi21e.Entities.Role;
import lt.viko.eif.pi21e.Entities.User;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import lt.viko.eif.pi21e.Utility.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

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
    }
}
