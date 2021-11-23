package com.assignment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {
        NoSuchElementException.class,
        IndexOutOfBoundsException.class,
        NullPointerException.class
    })
    public ResponseEntity<ErrorResponse> handleElementNotFoundException(Exception e) {
        return response(HttpStatus.NOT_FOUND, "Requested data not found");
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmeticException(Exception e) {
        return response(HttpStatus.BAD_REQUEST, "Bad numerical data");
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleSerializationException(Exception e) {
        return response(HttpStatus.BAD_REQUEST, "Invalid data format");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        final var message = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " - " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(MissingServletRequestParameterException e) {
        return response(HttpStatus.BAD_REQUEST, "Missing required request parameter - " + e.getParameterName());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        final var response = new ErrorResponse(status, message);
        return new ResponseEntity<>(response, status);
    }
}
