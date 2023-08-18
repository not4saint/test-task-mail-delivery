package com.example.testmaildelivery.exceptions;

public class PostalItemNotFoundException extends RuntimeException {
    public PostalItemNotFoundException(String message) {
        super(message);
    }
}
