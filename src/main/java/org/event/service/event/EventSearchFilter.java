package org.event.service.event;

import java.time.LocalDateTime;

public record EventSearchFilter(
        Integer durationMax,
        LocalDateTime dateStartBefore,
        Integer placesMin,
        Long locationId,
        EventStatus eventStatus,
        String name,
        Integer placesMax,
        Integer costMin,
        LocalDateTime dateStartAfter,
        Integer costMax,
        Integer durationMin
) {
}
