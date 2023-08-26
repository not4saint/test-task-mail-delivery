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
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionController extends ResponseEntityExceptionHandler {
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({PostalItemNotFoundException.class, PostNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(RuntimeException e,
                                                                      HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(request.getRequestURI(), e.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({PostalItemNotEnRoute.class, PostalItemNotInThePostOffice.class, PostalItemAlreadyReceivedException.class})
    public ResponseEntity<ExceptionResponse> handleIncorrectSizePostFieldsException(RuntimeException e,
                                                                                    HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(request.getRequestURI(), e.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}
