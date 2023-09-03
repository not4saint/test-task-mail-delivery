package com.example.testmaildelivery.exceptions;

public class PostalItemNotInThePostOfficeException extends RuntimeException {
    public PostalItemNotInThePostOfficeException(String message) {
        super(message);
    }
}
