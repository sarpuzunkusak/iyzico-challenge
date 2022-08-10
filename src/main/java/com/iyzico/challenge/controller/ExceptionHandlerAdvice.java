package com.iyzico.challenge.controller;

import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            ProductNotFoundException exception) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<MultipleErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        MultipleErrorResponse response = new MultipleErrorResponse();

        exception
                .getFieldErrors()
                .forEach(fieldError -> response.getMessages().add(fieldError.getDefaultMessage()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleOutOfStockException(
            ProductOutOfStockException exception) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(
            PaymentFailedException exception) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
