package org.event.service.location;

import jakarta.validation.constraints.*;

public record LocationDto(
        @Null
        Long id,

        @NotBlank
        @Size(max = 15)
        String name,

        @NotBlank
        @Size(max = 30)
        String address,

        @NotNull
        @Min(0)
        Integer capacity,

        @NotBlank
        @Size(max = 50)
        String description
) {
}
