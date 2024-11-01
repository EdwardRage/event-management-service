package org.event.service.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
      @NotBlank
      @Size(min = 4)
      String login,

      @NotNull
      Integer age,

      @NotBlank
      @Size(min = 5)
      String password
) {
}
