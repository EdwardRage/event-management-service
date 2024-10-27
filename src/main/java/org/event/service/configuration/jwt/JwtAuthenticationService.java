package org.event.service.configuration.jwt;

import lombok.RequiredArgsConstructor;
import org.event.service.user.SignInRequest;
import org.event.service.user.UserJwt;
import org.event.service.user.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    public String authenticationUser(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );
        if (signInRequest.login().equals("admin")) {
            return jwtTokenManager.generateJwt(signInRequest.login(), UserRole.ADMIN.name());
        } else {
            return jwtTokenManager.generateJwt(signInRequest.login(), UserRole.USER.name());
        }
    }

    public UserJwt getCurrentAuthenticationUserOrThrow() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication not present");
        }
        return (UserJwt) authentication.getPrincipal();
    }
}