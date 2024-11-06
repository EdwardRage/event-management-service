package org.event.service.event.kafka;

public record FieldChangeInteger(
        Integer oldField,
        Integer newField
) {
}
