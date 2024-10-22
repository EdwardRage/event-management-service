package org.event.service.configuration.jwt;

import lombok.RequiredArgsConstructor;
import org.event.service.user.SignInRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        return jwtTokenManager.generateJwt(signInRequest.login());
    }
}
