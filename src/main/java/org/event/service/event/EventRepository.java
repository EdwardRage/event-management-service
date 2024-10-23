package org.event.service.event;

import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Modifying
    @Query("""
        update EventEntity ev
        set ev.date = :eventDate,
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

    List<EventEntity> findAllByOwnerId(Long ownerId);
}
