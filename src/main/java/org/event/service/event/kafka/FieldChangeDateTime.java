package org.event.service.event.kafka;

import java.time.LocalDateTime;

public record FieldChangeDateTime(
        LocalDateTime oldField,
        LocalDateTime newField
) {
}
