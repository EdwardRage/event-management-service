package org.event.service.user;

public record UserDto(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
