package org.event.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityConverter entityConverter;

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Login already taken");
        }
        var hashPass = passwordEncoder.encode(signUpRequest.password());
        var userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashPass,
                signUpRequest.age(),
                UserRole.USER
        );
        var saved = userRepository.save(userToSave);
        return entityConverter.toDomain(saved);
    }

    public User getUserById(Long userId) {
        return entityConverter.toDomain(
                userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
    }
}
