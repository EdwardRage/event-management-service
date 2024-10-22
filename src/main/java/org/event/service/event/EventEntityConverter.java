package org.event.service.event;

import org.springframework.stereotype.Component;

@Component
public class EventEntityConverter {

    public EventEntity toEntity(Event event) {
        return new EventEntity(
                event.id(),
                event.eventDate(),
                event.duration(),
                event.cost(),
                event.maxPlaces(),
                event.locationId(),
                event.name(),
                EventStatus.STARTED
        );
    }

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getEvent(),
                eventEntity.getDuration(),
                eventEntity.getCost(),
                eventEntity.getMaxPlaces(),
                eventEntity.getLocationId(),
                eventEntity.getName()
        );
    }
}
