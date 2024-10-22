package org.event.service.event;

import org.springframework.stereotype.Component;

@Component
public class EventDtoConverter {

    public EventDto toDto(Event event) {
        return new EventDto(
                event.id(),
                event.eventDate(),
                event.duration(),
                event.cost(),
                event.maxPlaces(),
                event.locationId(),
                event.name()
        );
    }

    public Event toDomain(EventDto eventDto) {
        return new Event(
                eventDto.id(),
                eventDto.date(),
                eventDto.duration(),
                eventDto.cost(),
                eventDto.maxPlaces(),
                eventDto.locationId(),
                eventDto.name()
        );
    }
}
