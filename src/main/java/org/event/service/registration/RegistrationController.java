package org.event.service.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.configuration.jwt.JwtAuthenticationService;
import org.event.service.event.EventDto;
import org.event.service.event.EventDtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("events/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final EventDtoConverter dtoConverter;
    private final JwtAuthenticationService authenticationService;

    @PostMapping("/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> userRegistrationForEvent(@PathVariable Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        registrationService.userRegistrationForEvent(eventId, currentUser.login());
        log.info("User={} successful register for event with eventId={}", currentUser.login(), eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/cancel/{eventId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        registrationService.cancelRegistration(eventId, currentUser.login());
        log.info("Registration cancel");
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<EventDto>> getEventsRegistrationByUser() {
        var currentUser = authenticationService.getCurrentAuthenticationUserOrThrow();

        List<EventDto> events = registrationService.getEventsByUser(currentUser.login()).stream()
                .map(dtoConverter::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }
}
