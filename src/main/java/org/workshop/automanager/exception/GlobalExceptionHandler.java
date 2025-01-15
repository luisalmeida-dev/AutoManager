package org.workshop.automanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.workshop.automanager.dto.response.GlobalExceptionResponseDTO;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalExceptionResponseDTO> handle(NotFoundException exception) {
        GlobalExceptionResponseDTO response = new GlobalExceptionResponseDTO();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(exception.getMessage());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
