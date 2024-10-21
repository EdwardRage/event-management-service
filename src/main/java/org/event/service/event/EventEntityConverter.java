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
                event.name()
        );
    }

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getEventDate(),
                eventEntity.getDuration(),
                eventEntity.getCost(),
                eventEntity.getMaxPlaces(),
                eventEntity.getLocationId(),
                eventEntity.getName()
        );
    }
}
