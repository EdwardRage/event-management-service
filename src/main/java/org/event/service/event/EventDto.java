package org.event.service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventDto(
        @Null
        Long id,

        @NotNull
        @Future
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime date,

        @NotNull
        @Positive
        Integer duration,

        @NotNull
        @PositiveOrZero
        Integer cost,

        @NotNull
        @Positive
        Integer maxPlaces,

        @NotNull
        Long locationId,

        @NotBlank
        String name
) {
}
