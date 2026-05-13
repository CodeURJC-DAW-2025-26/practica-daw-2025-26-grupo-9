package es.urjc.daw.equis.controller.rest;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.urjc.daw.equis.security.jwt.AuthResponse;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AuthResponse> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(404)
                .body(new AuthResponse(AuthResponse.Status.FAILURE, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(400)
                .body(new AuthResponse(AuthResponse.Status.FAILURE, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleGeneral(Exception e) {
        log.error("Unhandled exception in REST controller", e);
        return ResponseEntity.status(500)
                .body(new AuthResponse(AuthResponse.Status.FAILURE, e.getMessage() != null ? e.getMessage() : "Internal server error"));
    }
}
