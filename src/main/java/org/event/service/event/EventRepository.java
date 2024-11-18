package org.event.service.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            ev.location.id = :locationId,
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

    @Query("""
        select ev from EventEntity ev
        where ev.id in :eventIds
        """)
    List<EventEntity> findAllEvents(List<Long> eventIds);

    @Query("""
        select ev from EventEntity ev
        join fetch ev.registrationList
        where ev.status = 'WAIT_START' or ev.status = 'STARTED'
        """)
    List<EventEntity> findAllEventsByWaitStartOrStarted();

    @Query("""
            SELECT e FROM EventEntity e WHERE
            (:durationMax IS NULL OR e.duration <= :durationMax) AND
            (:durationMin IS NULL OR e.duration >= :durationMin) AND
            (cast(:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter) AND
            (cast(:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore) AND
            (:placesMax IS NULL OR e.maxPlaces <= :placesMax) AND
            (:placesMin IS NULL OR e.maxPlaces >= :placesMin) AND
            (:locationId IS NULL OR e.location.id = :locationId) AND
            (:eventStatus IS NULL OR e.status = :eventStatus) AND
            (:name IS NULL OR e.name LIKE CONCAT('%', :name, '%')) AND
            (:costMin IS NULL OR e.cost >= :costMin) AND
            (:costMax IS NULL OR e.cost <= :costMax)
            """)
    List<EventEntity> findWithFilters(
            @Param("durationMax") Integer durationMax,
            @Param("durationMin") Integer durationMin,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("placesMax") Integer placesMax,
            @Param("placesMin") Integer placesMin,
            @Param("locationId") Long locationId,
            @Param("eventStatus") EventStatus eventStatus,
            @Param("name") String name,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax);

    @Modifying
    @Query("""
        update EventEntity ev
        set ev.status = :status
        where ev.id = :eventId
        """)
    void updateEventByStatus(
            Long eventId,
            EventStatus status
    );
}