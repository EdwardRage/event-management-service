package org.event.service.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        String detailedMessage,
        LocalDateTime dateTime
) {

}
