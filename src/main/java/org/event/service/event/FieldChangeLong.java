package org.event.service.event;

public record FieldChangeLong(
        Long oldField,
        Long newField
) {
}
