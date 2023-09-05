package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.MailType;
import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MailControllerTestIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostalItemRepository postalItemRepository;
    @Autowired
    private PostOfficeRepository postOfficeRepository;

    private PostOffice firstPostOffice;
    private PostOffice secondPostOffice;
    private PostalItem postalItem;

    @BeforeAll
    void addPostOffices() {
        PostOffice postOffice = PostOffice.builder()
                .name("First office")
                .address("Kochetova 56")
                .build();
        firstPostOffice = postOfficeRepository.save(postOffice);

        postOffice = PostOffice.builder()
                .name("Second office")
                .address("Zenita 56")
                .build();
        secondPostOffice = postOfficeRepository.save(postOffice);

        PostalItem postalItem = PostalItem.builder()
                .personIndex(123)
                .mailStatus(MailStatus.EN_ROUTE)
                .personName("Nikita")
                .mailType(MailType.LETTER)
                .address("Kirova 67")
                .postOffices(new LinkedHashSet<>()).build();
        this.postalItem = postalItemRepository.save(postalItem);
    }

    @AfterAll
    void cleanDatabase() {
        postalItemRepository.deleteAll();
        postOfficeRepository.deleteAll();
    }

    @Test
    void shouldReturnOkResponse_AfterRegisterPostalItem() throws Exception {
        var request = post("/api/mail/register-postal-item")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "mailType": "PARCEL",
                                "personIndex": 342,
                                "address": "Mira 23",
                                "personName": "Vova",
                                "postOfficeId": %s
                            }""".formatted(firstPostOffice.getId()));

        this.mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkResponse_AfterAddingPostOffice() throws Exception {
        var request = patch("/api/mail/add-post-office")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(postalItem.getId(), secondPostOffice.getId()));

        this.mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkResponse_AfterLeftFromPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/left-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void shouldReturnPostalItemResponse_AfterGetStatusAndPostOffices() throws Exception {
        postalItem.addPostOffice(firstPostOffice);
        postalItemRepository.save(postalItem);

        var request = get("/api/mail/check-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.content().json("""
                        {
                             "mailStatus": %s,
                             "postOffices": [
                               {
                                 "id": %s,
                                 "name": "First office",
                                 "address": "Kochetova 56"
                               }
                             ]
                        }""".formatted(postalItem.getMailStatus(), firstPostOffice.getId())));
    }

    @Test
    void shouldReturnOkResponse_AfterReceivedPostalItem() throws Exception {
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/receive-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpect(status().isOk());
    }
}