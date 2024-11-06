package org.event.service.event.kafka;

public record FieldChangeString(
        String oldField,
        String newField
) {
}
