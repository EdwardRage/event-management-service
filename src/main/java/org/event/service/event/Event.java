package org.event.service.event;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,
        LocalDateTime eventDate,
        Integer duration,
        Integer cost,
        Integer maxPlaces,
        Long locationId,
        String name,
        Long ownerId,
        EventStatus status,
        Integer occupiedPlaces,
        List<Long> users
) {
}
