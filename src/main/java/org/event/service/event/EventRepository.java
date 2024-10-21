package org.event.service.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
        update EventEntity ev
        set ev.eventDate = :eventDate,
            ev.duration = :duration,
            ev.cost = :cost,
            ev.maxPlaces = :maxPlaces,
            ev.locationId = :locationId,
            ev.name = :name
        where ev.id = :eventId
""")
    void updateEvent(
            Long eventId,
            LocalDateTime eventDate,
            Integer duration,
            Integer cost,
            Integer maxPlaces,
            Long locationId,
            String name
    );
}
