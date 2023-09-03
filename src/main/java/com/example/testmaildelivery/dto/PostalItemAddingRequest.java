package com.example.testmaildelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostalItemAddingRequest {
    private long id;
    private long postOfficeId;
}
