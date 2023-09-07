package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.exceptions.*;
import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.MailType;
import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
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
    void shouldReturnExceptionResponse_IfThrowPostOfficeNotFoundException_WhenRegisterPostalItem() throws Exception {
        long nonExistPostOfficeId = secondPostOffice.getId() + 1;
        var request = post("/api/mail/register-postal-item")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "mailType": "PARCEL",
                                "personIndex": 342,
                                "address": "Mira 23",
                                "personName": "Vova",
                                "postOfficeId": %s
                            }""".formatted(nonExistPostOfficeId));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostOfficeNotFoundException),
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/register-postal-item"),
                jsonPath("$.message").value("Post office with id=" + nonExistPostOfficeId
                                                                                    + " not found"),
                jsonPath("$.currentTime").exists()
        );
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
    void shouldReturnExceptionResponse_IfThrowPostalItemNotFoundException_WhenAddingPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);

        long nonExistPostalItemId = postalItem.getId() + 1;
        var request = patch("/api/mail/add-post-office")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(nonExistPostalItemId, secondPostOffice.getId()));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemNotFoundException),
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/add-post-office"),
                jsonPath("$.message").value("Postal item with id=" + nonExistPostalItemId
                        + " not found"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemAlreadyReceivedException_WhenAddingPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.RECEIVED);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/add-post-office")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(postalItem.getId(), secondPostOffice.getId()));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemAlreadyReceivedException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/add-post-office"),
                jsonPath("$.message").value("The postal item with id="
                        + postalItem.getId() + " has already been received"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemNotEnRouteException_WhenAddingPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/add-post-office")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(postalItem.getId(), secondPostOffice.getId()));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemNotEnRouteException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/add-post-office"),
                jsonPath("$.message").value("Postal item with id=" + postalItem.getId()
                        + " isn't transit"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostOfficeNotFoundException_WhenAddingPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);

        long nonExistPostOffice = secondPostOffice.getId() + 1;
        var request = patch("/api/mail/add-post-office")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(postalItem.getId(), nonExistPostOffice));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostOfficeNotFoundException),
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/add-post-office"),
                jsonPath("$.message").value("Post office with id="
                        + nonExistPostOffice + " not found"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemAlreadyBeenInPostOfficeException_WhenAddingPostOffice() throws Exception {
        postalItem.addPostOffice(secondPostOffice);
        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/add-post-office")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "id": %s,
                                "postOfficeId": %s
                            }""".formatted(postalItem.getId(), secondPostOffice.getId()));

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemAlreadyBeenInPostOfficeException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/add-post-office"),
                jsonPath("$.message").value("The postal item with id="
                        + postalItem.getId() + " has already been in post office with id="
                        + secondPostOffice.getId()),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnOkResponse_AfterLeftFromPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/left-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemAlreadyReceivedException_WhenLeftFromPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.RECEIVED);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/left-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemAlreadyReceivedException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/left-postal-item/" + postalItem.getId()),
                jsonPath("$.message").value("The postal item with id="
                        + postalItem.getId() + " has already been received"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemNotInThePostOfficeException_WhenLeftFromPostOffice() throws Exception {
        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/left-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemNotInThePostOfficeException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/left-postal-item/" + postalItem.getId()),
                jsonPath("$.message").value("Postal item with id=" + postalItem.getId()
                        + " isn't in the post office"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnPostalItemResponse_AfterGetStatusAndPostOffices() throws Exception {
        postalItem.addPostOffice(firstPostOffice);
        postalItemRepository.save(postalItem);

        var request = get("/api/mail/check-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
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

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemAlreadyReceivedException_WhenReceivedPostalItem() throws Exception {
        postalItem.setMailStatus(MailStatus.RECEIVED);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/receive-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemAlreadyReceivedException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/receive-postal-item/" + postalItem.getId()),
                jsonPath("$.message").value("The postal item with id="
                        + postalItem.getId() + " has already been received"),
                jsonPath("$.currentTime").exists()
        );
    }

    @Test
    void shouldReturnExceptionResponse_IfThrowPostalItemNotInThePostOfficeException_WhenReceivedPostalItem() throws Exception {
        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);

        var request = patch("/api/mail/receive-postal-item/{id}", postalItem.getId());

        this.mockMvc.perform(request).andExpectAll(
                result -> Assertions.assertTrue(result.getResolvedException() instanceof PostalItemNotInThePostOfficeException),
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.requestURI").value("/api/mail/receive-postal-item/" + postalItem.getId()),
                jsonPath("$.message").value("Postal item with id=" + postalItem.getId()
                        + " isn't in the post office"),
                jsonPath("$.currentTime").exists()
        );
    }
}