package org.event.service.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.configuration.jwt.JwtAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDtoConverter dtoConverter;
    private final JwtAuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Register new user={}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(user));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dtoConverter.toDto(
                        userService.getUserById(userId)
                )
        );
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authentication(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request sign in login={}", signInRequest.login());

        var token = authenticationService.authenticationUser(signInRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }
}
