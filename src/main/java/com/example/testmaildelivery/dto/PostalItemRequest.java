package com.example.testmaildelivery.dto;

import com.example.testmaildelivery.models.PostOffice;
import lombok.Data;

@Data
public class PostalItemRequest {
    private long id;
    private PostOffice postOffice;
}
