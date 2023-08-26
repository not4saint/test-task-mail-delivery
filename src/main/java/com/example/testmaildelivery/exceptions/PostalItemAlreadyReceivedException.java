package com.example.testmaildelivery.exceptions;

public class PostalItemAlreadyReceivedException extends RuntimeException {
    public PostalItemAlreadyReceivedException(String message) {
        super(message);
    }
}
