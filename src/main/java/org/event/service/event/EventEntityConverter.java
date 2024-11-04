package org.event.service.event;

import org.springframework.stereotype.Component;

@Component
public class EventEntityConverter {

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getDate(),
                eventEntity.getDuration(),
                eventEntity.getCost(),
                eventEntity.getMaxPlaces(),
                eventEntity.getLocation().getId(),
                eventEntity.getName(),
                eventEntity.getOwner().getId(),
                eventEntity.getStatus(),
                eventEntity.getOccupiedPlaces()
        );
    }
}
