package org.event.service.user;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank
        String login,
        @NotBlank
        String password
) {
}