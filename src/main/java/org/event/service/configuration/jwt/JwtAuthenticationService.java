package org.event.service.configuration.jwt;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.event.service.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;
    private final UserEntityConverter entityConverter;

    public String authenticationUser(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );
        User user = entityConverter.toDomain(
                userRepository.findByLogin(signInRequest.login())
                        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        );
        return jwtTokenManager.generateJwt(user);
    }

    public UserJwt getCurrentAuthenticationUserOrThrow() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication not present");
        }
        return (UserJwt) authentication.getPrincipal();
    }
}