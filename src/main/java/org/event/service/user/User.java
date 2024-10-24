package org.event.service.user;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
