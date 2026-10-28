package com.ledgerflow.common.exception;

import com.ledgerflow.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(
            EmailAlreadyRegisteredException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .orElse("Invalid request");

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                message,
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequestBody(
            HttpMessageNotReadableException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                "Invalid or malformed request body",
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                "Request validation failed",
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(
            Exception ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                "An unexpected server error occurred",
                null,
                java.time.Instant.now()
        );
        log.error("Unexpected server error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(
            ResponseStatusException ex
    ) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                ex.getReason(),
                null,
                java.time.Instant.now()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }
}
