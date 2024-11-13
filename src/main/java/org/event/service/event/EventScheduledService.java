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
        List<EventEntity> events = eventRepository.findAllEventsByWaitStartOrStarted();
        LocalDateTime timeNow = LocalDateTime.now();

        for (EventEntity event : events) {
            LocalDateTime eventStart = event.getDate();
            LocalDateTime eventEnd = event.getDate().plusMinutes(event.getDuration());

            if (event.getStatus().equals(EventStatus.WAIT_START)
                    && eventStart.isBefore(timeNow)) {

                List<Long> users = getUsersIds(event);
                Event eventDomain = entityConverter.toDomain(event);
                sendKafka(eventDomain, EventStatus.STARTED, users);

                event.setStatus(EventStatus.STARTED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
            if (event.getStatus().equals(EventStatus.STARTED)
                    && eventEnd.isBefore(timeNow)) {

                List<Long> users = getUsersIds(event);
                Event eventDomain = entityConverter.toDomain(event);
                sendKafka(eventDomain, EventStatus.FINISHED, users);

                event.setStatus(EventStatus.FINISHED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
        }
    }

    private List<Long> getUsersIds(EventEntity event) {
        return event.getRegistrationList().stream()
                .map(reg -> reg.getUser().getId())
                .toList();
    }

    private void sendKafka(Event event, EventStatus status, List<Long> users) {
        eventKafkaSender.sendEventChangeNotification(new EventChangeNotification(
                event.id(),
                event.ownerId(),
                users,
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
