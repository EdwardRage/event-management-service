package org.event.service.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.event.service.location.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventEntityConverter entityConverter;
    private final EntityManager entityManager;
    private final LocationRepository locationRepository;

    public Event createEvent(Event newEvent) {
        var event = entityConverter.toEntity(newEvent);

        return entityConverter.toDomain(
            eventRepository.save(event)
        );
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        eventRepository.delete(event);
    }

    public Event getEventById(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = " + eventId + " not found"));

        return entityConverter.toDomain(event);
    }

    @Transactional
    public Event updateEvent(Long eventId, EventDto eventDto) {
        if (eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id = " + eventId + " not found");
        }

        eventRepository.updateEvent(
            eventId,
            eventDto.eventDate(),
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
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), eventSearchFilter.dateStartBefore())
            );
        }
        if (eventSearchFilter.dateStartAfter() != null) {
            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), eventSearchFilter.dateStartAfter())
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
            if (locationRepository.existsById(eventSearchFilter.locationId())) {
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
}
