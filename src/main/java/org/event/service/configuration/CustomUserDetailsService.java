package org.event.service.configuration;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.event.service.user.UserEntity;
import org.event.service.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return User.withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRole().toString())
                .build();
    }
}
