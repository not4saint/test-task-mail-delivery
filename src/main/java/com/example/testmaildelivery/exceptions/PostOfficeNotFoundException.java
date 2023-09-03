package com.example.testmaildelivery.exceptions;

public class PostOfficeNotFoundException extends RuntimeException {
    public PostOfficeNotFoundException(String message) {
        super(message);
    }
}
