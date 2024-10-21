package org.event.service.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record EventDto(
        @Null
        Long id,

        @NotNull
        LocalDateTime eventDate,

        @NotNull
        Integer duration,

        @NotNull
        Integer cost,

        @NotNull
        Integer maxPlaces,

        @NotNull
        Long locationId,

        @NotBlank
        String name
) {
}
