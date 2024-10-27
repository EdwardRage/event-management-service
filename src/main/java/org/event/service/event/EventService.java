package org.event.service.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.event.service.event.registration.RegistrationEntity;
import org.event.service.event.registration.RegistrationRepository;
import org.event.service.location.LocationRepository;
import org.event.service.user.UserEntity;
import org.event.service.user.UserRepository;
import org.event.service.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventEntityConverter entityConverter;
    private final EntityManager entityManager;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

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
                newEvent.locationId(),
                newEvent.name(),
                owner.getId(),
                EventStatus.WAIT_START.name(),
                0
        );

        return entityConverter.toDomain(
            eventRepository.save(event)
        );
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteEvent(Long eventId, String login) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        checkDenied(event, user);
        checkEventStatus(event);
        checkEventStatusForStarted(event);

        event.setStatus(EventStatus.CLOSED.name());
        eventRepository.save(event);
    }

    public Event getEventById(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        return entityConverter.toDomain(event);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Event updateEvent(Long eventId, EventDto eventDto, String login) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        var user = userRepository.findByLogin(login)
                .orElseThrow();
        checkDenied(event, user);
        checkEventStatus(event);
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

        return entityConverter.toDomain(
                eventRepository.findById(eventId).orElseThrow()
        );
    }

    public List<Event> getEventWithFilter(EventSearchFilter eventSearchFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> criteriaQuery = criteriaBuilder.createQuery(EventEntity.class);
        Root<EventEntity> root = criteriaQuery.from(EventEntity.class);

        Predicate predicates = criteriaBuilder.conjunction();

        if(eventSearchFilter.durationMax() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.lessThanOrEqualTo(root.get("duration"), eventSearchFilter.durationMax())
            );
        }
        if(eventSearchFilter.durationMin() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), eventSearchFilter.durationMin())
            );
        }
        if (eventSearchFilter.dateStartBefore() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), eventSearchFilter.dateStartBefore())
            );
        }
        if (eventSearchFilter.dateStartAfter() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), eventSearchFilter.dateStartAfter())
            );
        }
        if (eventSearchFilter.placesMax() != null) {
            predicates = criteriaBuilder.and(
                predicates,
                criteriaBuilder.lessThanOrEqualTo(root.get("maxPlaces"), eventSearchFilter.placesMax())
            );
        }
        if (eventSearchFilter.placesMin() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), eventSearchFilter.placesMin())
            );
        }
        if (eventSearchFilter.locationId() != null) {
            if (!locationRepository.existsById(eventSearchFilter.locationId())) {
                throw new EntityNotFoundException(
                        "Location with id = " + eventSearchFilter.locationId() + " not found");
            }
            predicates = criteriaBuilder.and(
                predicates,
                criteriaBuilder.equal(root.get("locationId"), eventSearchFilter.locationId())
            );
        }
        if (eventSearchFilter.eventStatus() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.equal(root.get("status"), eventSearchFilter.eventStatus())
            );
        }
        if (eventSearchFilter.name() != null && !eventSearchFilter.name().isEmpty()) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.like(root.get("name"), "%" + eventSearchFilter.name() + "%")
            );
        }
        if (eventSearchFilter.costMin() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), eventSearchFilter.costMin())
            );
        }
        if (eventSearchFilter.costMax() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.lessThanOrEqualTo(root.get("cost"), eventSearchFilter.costMax())
            );
        }

        criteriaQuery.where(predicates);

        return entityManager.createQuery(criteriaQuery)
                .getResultList().stream()
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

    @Transactional(propagation = Propagation.SUPPORTS)
    public void userRegistrationForEvent(Long eventId, String login) {

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        var user = userRepository.findByLogin(login)
                .orElseThrow();

        checkEventStatus(event);
        checkRegistrationCondition(event, user);

        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);

        var registration = new RegistrationEntity(
                null,
                event.getId(),
                user.getId(),
                LocalDateTime.now()
        );
        registrationRepository.save(registration);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void canselRegistration(Long eventId, String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow();

        var registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("user is not registered"));
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        checkEventStatus(event);
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("Already cannot cansel registration");
        }
        registrationRepository.delete(registration);

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    private void checkRegistrationCondition(EventEntity event, UserEntity user) {
        if (event.getOwnerId().equals(user.getId())) {
            throw new IllegalArgumentException("Owner can't register for event");
        }
        if (!event.getStatus().equals(EventStatus.WAIT_START.name()) && !event.getStatus().equals(EventStatus.STARTED.name())) {
            throw new IllegalArgumentException("Registration is already closed");
        }
        if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new IllegalArgumentException("Sold out!");
        }
        if (registrationRepository.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new IllegalArgumentException("The user is already registered for the event");
        }
    }

    private void checkDenied(EventEntity event, UserEntity user) {
        if (!event.getOwnerId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN.name())) {
            throw new IllegalArgumentException("Only the admin or owner can delete an event");
        }
    }

    private void checkEventStatus(EventEntity event) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (event.getStatus().equals(EventStatus.CLOSED.name())) {
            throw new IllegalArgumentException("This event already CLOSED");
        }
        if (event.getDate().isBefore(localDateTime)
            && event.getDate().plusMinutes(event.getDuration()).isAfter(LocalDateTime.now())
        ) {
            event.setStatus(EventStatus.STARTED.name());
            eventRepository.save(event);
        } else if (event.getDate().plusMinutes(event.getDuration()).isBefore(LocalDateTime.now())) {
            event.setStatus(EventStatus.FINISHED.name());
            eventRepository.save(event);
        }
    }

    private void checkEventStatusForStarted(EventEntity event) {
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("the event cannot be cancelled or update");
        }
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
}
