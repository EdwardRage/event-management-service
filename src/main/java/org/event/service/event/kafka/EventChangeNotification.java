package org.event.service.event.kafka;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record EventChangeNotification(
        Long eventId,

        Long ownerId,

        List<Long> users,

        @Nullable
        Long changedId,
        @Nullable
        FieldChange<String> name,
        FieldChange<Integer> maxPlace,
        FieldChange<LocalDateTime> date,
        FieldChange<Integer> cost,
        FieldChange<Integer> duration,
        FieldChange<Long> locationId,
        FieldChange<String> status
) {
}
