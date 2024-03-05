package lt.viko.eif.pi21e.Authentication;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Role;
import lt.viko.eif.pi21e.Entities.User;
import lt.viko.eif.pi21e.Repositories.UserRepository;
import lt.viko.eif.pi21e.Utility.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // TODO: Add logic to not duplicate usernames.

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public Boolean validate(ValidationRequest request) throws Exception {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

//        if (!user.getPassword().equals(request.getPassword()))
//            throw new Exception("Invalid password.");

        return jwtService.isTokenValid(request.getToken(), user);
    }
}


