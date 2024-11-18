package org.event.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.event.service.event.kafka.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventScheduledService {
    private final EventRepository eventRepository;
    private final EventKafkaSender eventKafkaSender;
    private final EventEntityConverter entityConverter;

    @Scheduled(fixedRate = 60_000L)
    public void eventUpdateStatus() {

        List<Event> eventDomain = eventRepository.findAllEventsByWaitStartOrStarted().stream()
                .map(entityConverter::toDomain)
                .toList();

        LocalDateTime timeNow = LocalDateTime.now();

        for (Event event : eventDomain) {
            LocalDateTime eventStart = event.eventDate();
            LocalDateTime eventEnd = event.eventDate().plusMinutes(event.duration());

            if (event.status().equals(EventStatus.WAIT_START)
                    && eventStart.isBefore(timeNow)) {

                eventRepository.updateEventByStatus(event.id(), EventStatus.STARTED);
                sendKafka(event, EventStatus.STARTED);
                log.info("event with id={} update status={}", event.id(), event.status());

            }
            if (event.status().equals(EventStatus.STARTED)
                    && eventEnd.isBefore(timeNow)) {

                eventRepository.updateEventByStatus(event.id(), EventStatus.FINISHED);
                sendKafka(event, EventStatus.FINISHED);
                log.info("event with id={} update status={}", event.id(), event.status());
            }
        }
    }

    private void sendKafka(Event event, EventStatus status) {
        eventKafkaSender.sendEventChangeNotification(new EventChangeNotification(
                event.id(),
                event.ownerId(),
                event.users(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new FieldChange<>(event.status().name(), status.name())
        ));
    }
}
