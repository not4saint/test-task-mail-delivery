package com.example.testmaildelivery.dto;

import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.PostOffice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PostalItemResponse {
    private MailStatus mailStatus;

    private Set<PostOffice> postOffices;
}
