package org.event.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.event.kafka.*;
import org.event.service.location.LocationRepository;
import org.event.service.user.UserEntity;
import org.event.service.user.UserRepository;
import org.event.service.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventEntityConverter entityConverter;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventKafkaSender eventKafkaSender;

    public Event createEvent(Event newEvent, String login) {
        var locationEvent = locationRepository.findById(newEvent.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id = " + newEvent.locationId() + " not found"));

        var owner = userRepository.findByLogin(login)
                .orElseThrow();

        if (locationEvent.getCapacity() < newEvent.maxPlaces()) {
            throw new IllegalArgumentException("Площадка не может вместить заявленное количество участников");
        }

        var event = new EventEntity(
                newEvent.id(),
                newEvent.eventDate(),
                newEvent.duration(),
                newEvent.cost(),
                newEvent.maxPlaces(),
                locationEvent,
                newEvent.name(),
                owner,
                EventStatus.WAIT_START,
                0
        );

        return entityConverter.toDomain(
            eventRepository.save(event)
        );
    }

    public void deleteEvent(Long eventId, String login) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        checkDenied(event, user);
        checkEventStatusForStarted(event);

        event.setStatus(EventStatus.CLOSED);
        eventRepository.save(event);
    }

    public Event getEventById(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        return entityConverter.toDomain(event);
    }

    @Transactional()
    public Event updateEvent(Long eventId, EventDto eventDto, String login) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        var user = userRepository.findByLogin(login)
                .orElseThrow();
        checkDenied(event, user);
        checkEventStatusForStarted(event);

        eventRepository.updateEvent(
            eventId,
            eventDto.date(),
            eventDto.duration(),
            eventDto.cost(),
            eventDto.maxPlaces(),
            eventDto.locationId(),
            eventDto.name()
        );

        eventKafkaSender.setEventChangeNotification(new EventChangeNotification(
                event.getId(),
                new FieldChangeString(event.getName(), eventDto.name()),
                new FieldChangeInteger(event.getMaxPlaces(), eventDto.maxPlaces()),
                new FieldChangeDateTime(event.getDate(), eventDto.date()),
                new FieldChangeInteger(event.getCost(), eventDto.cost()),
                new FieldChangeInteger(event.getDuration(), eventDto.duration()),
                new FieldChangeLong(event.getLocation().getId(), eventDto.locationId())
        ));

        return entityConverter.toDomain(
                eventRepository.findById(eventId).orElseThrow()
        );
    }

    public List<Event> getEventWithFilter(EventSearchFilter eventSearchFilter) {
        List<EventEntity> events = eventRepository.findWithFilters(
                eventSearchFilter.durationMax(),
                eventSearchFilter.durationMin(),
                eventSearchFilter.dateStartBefore(),
                eventSearchFilter.dateStartAfter(),
                eventSearchFilter.placesMax(),
                eventSearchFilter.placesMin(),
                eventSearchFilter.locationId(),
                eventSearchFilter.eventStatus(),
                eventSearchFilter.name(),
                eventSearchFilter.costMin(),
                eventSearchFilter.costMax()
        );

        return events.stream()
                .map(entityConverter::toDomain)
                .toList();

    }

    public List<Event> getEventsByOwner(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        return eventRepository.findAllByOwnerId(user.getId()).stream()
                .map(entityConverter::toDomain)
                .toList();
    }



    private void checkDenied(EventEntity event, UserEntity user) {
        if (!event.getOwner().getId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("Only the admin or owner can delete an event");
        }
    }

    private void checkEventStatusForStarted(EventEntity event) {

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("the event cannot be cancelled or update");
        }
    }
}
