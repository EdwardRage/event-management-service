package org.event.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventScheduledService {
    private final EventRepository eventRepository;

    @Scheduled(fixedRate = 60_000L)
    public void eventUpdateStatus() {
        List<EventEntity> events = eventRepository.findAllEventsByWaitStartOrStarted();
        LocalDateTime timeNow = LocalDateTime.now();

        for (EventEntity event : events) {
            LocalDateTime eventStart = event.getDate();
            LocalDateTime eventEnd = event.getDate().plusMinutes(event.getDuration());

            if (event.getStatus().equals(EventStatus.WAIT_START)
                    && eventStart.isBefore(timeNow)) {
                event.setStatus(EventStatus.STARTED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
            if (event.getStatus().equals(EventStatus.STARTED)
                    && eventEnd.isBefore(timeNow)) {
                event.setStatus(EventStatus.FINISHED);
                eventRepository.save(event);
                log.info("event with id={} update status={}", event.getId(), event.getStatus());
            }
        }
    }
}
