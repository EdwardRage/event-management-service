package org.event.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityConverter entityConverter;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initializeUser() {
        if (!userRepository.existsByLogin("admin")) {
            var hashPass = passwordEncoder.encode("admin");
            UserEntity admin = new UserEntity(
                    null,
                    "admin",
                    hashPass,
                    25,
                    UserRole.ADMIN.name()
            );
            userRepository.save(admin);
            log.info("Default admin created");
        } else {
            log.info("Default admin already exists");
        }

        if (!userRepository.existsByLogin("user")) {
            var hashPass = passwordEncoder.encode("user");
            UserEntity user = new UserEntity(
                    null,
                    "user",
                    hashPass,
                    25,
                    UserRole.USER.name()
            );
            userRepository.save(user);
            log.info("Default user created");
        } else {
            log.info("Default user already exists");
        }
    }

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
                UserRole.USER.name()
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
