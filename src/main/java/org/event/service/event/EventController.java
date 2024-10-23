package org.event.service.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.configuration.jwt.JwtAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventDtoConverter dtoConverter;
    private final JwtAuthenticationService authenticationService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventDto eventDto) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        Event eventDomain = dtoConverter.toDomainWithOwnerId(eventDto, currentUser.id());
        EventDto event = dtoConverter.toDto(eventService.createEvent(eventDomain));
        log.info("Create new event: {}", eventDto.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    /*удалить мероприятие может только админ или организатор мероприятия*/
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        eventService.deleteEvent(eventId, currentUser.id());
        log.info("Event with id={} delete", eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dtoConverter.toDto(
                        eventService.getEventById(eventId)
                )
        );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId,
                                                @RequestBody @Valid EventDto eventDto) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        EventDto eventResponse = dtoConverter.toDto(
                eventService.updateEvent(eventId, eventDto, currentUser.id())
        );
        log.info("Event with id={} update", eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchFilter eventSearchFilter) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        eventService.getEventWithFilter(eventSearchFilter).stream()
                                .map(dtoConverter::toDto)
                                .toList()
                );
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getEventsByUser() {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();
        List<EventDto> events = eventService.getEventsByOwnerId(currentUser.id()).stream()
                .map(dtoConverter::toDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

    @PostMapping("/registrations/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> userRegistrationForEvent(@PathVariable Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        eventService.userRegistrationForEvent(eventId, currentUser.id());
        log.info("User with userId={} successful register for event withe eventId={}", currentUser.id(), eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}