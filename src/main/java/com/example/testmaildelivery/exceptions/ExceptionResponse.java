package com.example.testmaildelivery.exceptions;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ExceptionResponse {
    private String requestURI;
    private String message;
    private LocalDateTime currentTime;
}
