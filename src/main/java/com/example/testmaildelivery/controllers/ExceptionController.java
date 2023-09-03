package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler({PostalItemNotFoundException.class, PostOfficeNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(RuntimeException e,
                                                                      HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(request.getRequestURI(), e.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PostalItemNotEnRouteException.class, PostalItemNotInThePostOffice.class,
                        PostalItemAlreadyReceivedException.class, PostalItemAlreadyBeenInPostOfficeException.class})
    public ResponseEntity<ExceptionResponse> handleIncorrectSizePostFieldsException(RuntimeException e,
                                                                                    HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(request.getRequestURI(), e.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
