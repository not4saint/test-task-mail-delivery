package com.example.testmaildelivery.exceptions;

public class PostalItemNotInThePostOffice extends RuntimeException {
    public PostalItemNotInThePostOffice(String message) {
        super(message);
    }
}
