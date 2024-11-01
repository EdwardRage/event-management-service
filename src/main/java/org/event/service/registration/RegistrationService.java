package org.event.service.registration;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.event.service.event.*;
import org.event.service.user.UserEntity;
import org.event.service.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventEntityConverter entityConverter;

    @Transactional
    public void userRegistrationForEvent(Long eventId, String login) {

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        var user = userRepository.findByLogin(login)
                .orElseThrow();

        checkRegistrationCondition(event, user);

        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);

        var registration = new RegistrationEntity(
                null,
                event,
                user,
                LocalDateTime.now()
        );
        registrationRepository.save(registration);
    }

    @Transactional
    public void canselRegistration(Long eventId, String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        var registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("user is not registered"));
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Already cannot cansel registration");
        }
        registrationRepository.delete(registration);

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    public List<Event> getEventsByUser(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        List<Long> eventIds = registrationRepository.findEventIdsByUserId(user.getId());

        List<EventEntity> eventsList = eventRepository.findAllEvents(eventIds);

        return eventsList.stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    private void checkRegistrationCondition(EventEntity event, UserEntity user) {
        if (event.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Owner can't register for event");
        }
        if (!event.getStatus().equals(EventStatus.WAIT_START)
                && !event.getStatus().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Registration is already closed");
        }
        if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new IllegalArgumentException("Sold out!");
        }
        if (registrationRepository.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new IllegalArgumentException("The user is already registered for the event");
        }
    }
}
