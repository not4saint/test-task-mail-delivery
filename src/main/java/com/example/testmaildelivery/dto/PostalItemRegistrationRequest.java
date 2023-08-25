package com.example.testmaildelivery.dto;

import com.example.testmaildelivery.models.MailType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostalItemRegistrationRequest {
    @Enumerated(EnumType.STRING)
    private MailType mailType;

    @NotNull
    private long personIndex;

    @NotNull
    private String address;

    @NotNull
    private String personName;

    @NotNull
    private long postOfficeId;
}
