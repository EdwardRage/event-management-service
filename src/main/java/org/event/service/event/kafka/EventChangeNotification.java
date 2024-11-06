package org.event.service.event.kafka;

import org.event.service.event.FieldChangeLong;

public record EventChangeNotification(
        Long eventId,
        FieldChangeString name,
        FieldChangeInteger maxPlace,
        FieldChangeDateTime date,
        FieldChangeInteger cost,
        FieldChangeInteger duration,
        FieldChangeLong locationId
) {
}
