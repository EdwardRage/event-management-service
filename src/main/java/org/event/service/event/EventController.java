package org.event.service.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventDtoConverter dtoConverter;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventDto eventDto) {
        Event eventDomain = dtoConverter.toDomain(eventDto);
        EventDto event = dtoConverter.toDto(eventService.createEvent(eventDomain));
        log.info("Create new event");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
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
        EventDto eventResponse = dtoConverter.toDto(
                eventService.updateEvent(eventId, eventDto)
        );
        log.info("Event with id={} update", eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchFilter eventSearchFilter) {
        eventService.getEventWithFilter(eventSearchFilter);
    }
}
