package com.example.testmaildelivery.exceptions;

public class PostalItemAlreadyBeenInPostOffice extends RuntimeException {
    public PostalItemAlreadyBeenInPostOffice(String message) {
        super(message);
    }
}
