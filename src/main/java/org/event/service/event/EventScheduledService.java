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

    @Scheduled(fixedRate = 60_000L)
    public void eventUpdateStatus() {
        List<EventEntity> events = eventRepository.findAllEventsByWaitStartOrStarted();
        LocalDateTime timeNow = LocalDateTime.now();

        for (EventEntity event : events) {
            LocalDateTime eventStart = event.getDate();
            LocalDateTime eventEnd = event.getDate().plusMinutes(event.getDuration());

            if (event.getStatus().equals(EventStatus.WAIT_START)
                    && eventStart.isBefore(timeNow)) {

                sendKafka(event, EventStatus.STARTED);

                event.setStatus(EventStatus.STARTED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
            if (event.getStatus().equals(EventStatus.STARTED)
                    && eventEnd.isBefore(timeNow)) {

                sendKafka(event, EventStatus.FINISHED);

                event.setStatus(EventStatus.FINISHED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
        }
    }

    private void sendKafka(EventEntity event, EventStatus status) {
        List<Long> users = event.getRegistrationList().stream()
                        .map(reg -> reg.getUser().getId())
                        .toList();
        eventKafkaSender.setEventChangeNotification(new EventChangeNotification(
                event.getId(),
                event.getOwner().getId(),
                users,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new FieldChange<>(event.getStatus().name(), status.name())
        ));
    }
}
