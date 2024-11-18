package org.event.service.user;

public record UserJwt(
        String login,
        String role,
        Long id
) {
}
