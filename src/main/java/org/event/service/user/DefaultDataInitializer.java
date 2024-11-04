package org.event.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeUser() {
        if (!userRepository.existsByLogin("admin")) {
            var hashPass = passwordEncoder.encode("admin");
            UserEntity admin = new UserEntity(
                    null,
                    "admin",
                    hashPass,
                    25,
                    UserRole.ADMIN
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
                    UserRole.USER
            );
            userRepository.save(user);
            log.info("Default user created");
        } else {
            log.info("Default user already exists");
        }
    }
}
