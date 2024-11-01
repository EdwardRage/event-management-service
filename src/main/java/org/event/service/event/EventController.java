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

        Event eventDomain = dtoConverter.toDomain(eventDto);
        EventDto event = dtoConverter.toDto(eventService.createEvent(eventDomain, currentUser.login()));
        log.info("Create new event: {}", eventDto.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        eventService.deleteEvent(eventId, currentUser.login());
        log.info("Event with id={} delete", eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dtoConverter.toDto(
                        eventService.getEventById(eventId)
                )
        );
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId,
                                                @RequestBody @Valid EventDto eventDto) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        EventDto eventResponse = dtoConverter.toDto(
                eventService.updateEvent(eventId, eventDto, currentUser.login())
        );
        log.info("Event with id={} update", eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventResponse);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
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

        List<EventDto> events = eventService.getEventsByOwner(currentUser.login()).stream()
                .map(dtoConverter::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }
}