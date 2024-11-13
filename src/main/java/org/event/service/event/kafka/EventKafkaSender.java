package org.event.service.event.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaSender {
    private final KafkaTemplate<Long, EventChangeNotification> kafkaTemplate;

    public void sendEventChangeNotification(EventChangeNotification eventChange) {
        log.info("Sending event={}", eventChange);
        var result = kafkaTemplate.send(
                "event-topic",
                eventChange.eventId(),
                eventChange);

    }
}
