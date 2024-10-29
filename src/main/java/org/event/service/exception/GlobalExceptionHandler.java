package org.event.service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            EntityNotFoundException.class
    )
    public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception e) {
        log.error("Got exception ", e);
        var exception = new ExceptionResponse(
                "Сущность не найдена",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exception);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorization(
            AuthorizationDeniedException e
    ) {
        log.error("Handle authorization exception", e);

        var errorDto = new ExceptionResponse(
                "Forbidden",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorDto);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ExceptionResponse> handleValidationException(
            Exception e
    ) {
        log.error("Got validation exception", e);

        String detailedMessage = e instanceof MethodArgumentNotValidException
                ? constructMethodArgumentNotValidMessage((MethodArgumentNotValidException) e)
                : e.getMessage();

        var errorDto =  new ExceptionResponse(
                "Ошибка валидации запроса",
                detailedMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleGenerisException(
            Exception e
    ) {
        log.error("Server error", e);
        var errorDto =  new ExceptionResponse(
                "Server error",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }

    private static String constructMethodArgumentNotValidMessage(
            MethodArgumentNotValidException e
    ) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
