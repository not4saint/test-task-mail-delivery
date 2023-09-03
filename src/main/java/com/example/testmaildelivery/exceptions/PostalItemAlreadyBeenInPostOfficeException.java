package com.example.testmaildelivery.exceptions;

public class PostalItemAlreadyBeenInPostOfficeException extends RuntimeException {
    public PostalItemAlreadyBeenInPostOfficeException(String message) {
        super(message);
    }
}
