package org.event.service.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
