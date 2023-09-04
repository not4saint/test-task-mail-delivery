package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MailControllerTestIT {
    @Autowired
    private MockMvc mockMvc;
//    private MailController mailController;
//    private MailService mailService;
    @Autowired
    private PostalItemRepository postalItemRepository;
    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @AfterEach
    void cleanDatabase() {
        postalItemRepository.deleteAll();
        postOfficeRepository.deleteAll();
    }

    @Test
    void shouldReturnOkResponse_AfterRegisterPostalItem() throws Exception {
        PostOffice postOffice = PostOffice.builder().name("First office").address("Kochetova 56").build();
        postOffice = postOfficeRepository.save(postOffice);

        var request = post("/api/mail/register-postal-item")
                    .contentType(MediaType.APPLICATION_JSON).content("""
                            {
                                "mailType": "PARCEL",
                                "personIndex": 342,
                                "address": "Mira 23",
                                "personName": "Vova",
                                "postOfficeId": %s
                            }""".formatted(postOffice.getId()));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        );
    }



    @Test
    void addPostalItemToPostOffice() {
    }

    @Test
    void leftPostalItemFromPostOffice() {
    }

    @Test
    void checkPostalItem() {
    }

    @Test
    void receivePostalItem() {
    }
}