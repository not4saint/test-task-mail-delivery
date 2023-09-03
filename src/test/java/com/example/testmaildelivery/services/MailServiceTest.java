package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostalItemAddingRequest;
import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.exceptions.*;
import com.example.testmaildelivery.mappers.PostalItemMapper;
import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.MailType;
import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    private PostOffice firstPostOffice;
    private PostOffice secondPostOffice;
    private PostalItem postalItem;
    @Mock
    private PostalItemRepository postalItemRepository;
    @Mock
    private PostOfficeRepository postOfficeRepository;
    @Mock
    private PostalItemMapper postalItemMapper;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        firstPostOffice = new PostOffice(1, "First post office", "Stepanova 67");
        secondPostOffice = new PostOffice(2, "Second post office", "Larino 56");
        postalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.IN_THE_POST_OFFICE, new LinkedHashSet<>());
    }

    @Test
    void shouldReturnPostalItem_AfterRegister() {
        postalItem.addPostOffice(firstPostOffice);
        var registrationRequest = new PostalItemRegistrationRequest(MailType.LETTER,
                                                                    123,
                                                                    "Mira 23",
                                                                    "Peter",
                                                                    1);
        PostalItem resultPostalItem = PostalItem.builder()
                .id(1)
                .mailType(MailType.LETTER)
                .personIndex(123)
                .address("Mira 23")
                .personName("Peter")
                .postOffices(new LinkedHashSet<>())
                .build();

        when(postalItemMapper.toModel(any(PostalItemRegistrationRequest.class)))
                            .thenReturn(resultPostalItem);

        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.of(firstPostOffice));

        mailService.registerPostalItem(registrationRequest);

        assertNotNull(resultPostalItem);
        assertEquals(resultPostalItem.getPostOffices(), postalItem.getPostOffices());
        assertEquals(resultPostalItem, postalItem);
    }

    @Test
    void shouldThrowPostNotFoundException_IfNotExist() {
        var registrationRequest = new PostalItemRegistrationRequest(MailType.LETTER,
                123,
                "Mira 23",
                "Peter",
                2);

        when(postalItemMapper.toModel(any(PostalItemRegistrationRequest.class)))
                .thenReturn(PostalItem.builder()
                        .id(1)
                        .mailType(MailType.LETTER)
                        .personIndex(123)
                        .address("Mira 23")
                        .personName("Peter")
                        .postOffices(new LinkedHashSet<>())
                        .build());

        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        RuntimeException e = assertThrows(PostOfficeNotFoundException.class,
                () -> mailService.registerPostalItem(registrationRequest));
        assertEquals(e.getMessage(), "Post office with id="
                + registrationRequest.getPostOfficeId() + " not found");
    }

    @Test
    void shouldAddPostalItemToPostOffice_AfterRequest() {
        postalItem.addPostOffice(firstPostOffice);
        postalItem.addPostOffice(secondPostOffice);
        var resultPostalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.EN_ROUTE, new LinkedHashSet<>());
        resultPostalItem.addPostOffice(firstPostOffice);

        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(resultPostalItem));
        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.of(secondPostOffice));

        var addingRequest = new PostalItemAddingRequest(1, 2);
        mailService.addPostalItemToPostOffice(addingRequest);

        assertNotNull(resultPostalItem);
        assertEquals(resultPostalItem.getPostOffices(), postalItem.getPostOffices());
        assertEquals(resultPostalItem, postalItem);
    }

    @Test
    void shouldThrowPostalItemAlreadyReceivedException_IfItemReceived() {
        var addingRequest = new PostalItemAddingRequest(1, 2);
        var resultPostalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.RECEIVED, new LinkedHashSet<>());
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(resultPostalItem));

        RuntimeException e = assertThrows(PostalItemAlreadyReceivedException.class,
                 () -> mailService.addPostalItemToPostOffice(addingRequest));
        assertEquals(e.getMessage(), "The postal item with id="
                + resultPostalItem.getId() + " has already been received");
    }

    @Test
    void shouldThrowPostalItemNotEnRouteException_IfItemEnRoute() {
        var addingRequest = new PostalItemAddingRequest(1, 2);
        var resultPostalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.IN_THE_POST_OFFICE, new LinkedHashSet<>());
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(resultPostalItem));

        RuntimeException e = assertThrows(PostalItemNotEnRouteException.class,
                () -> mailService.addPostalItemToPostOffice(addingRequest));
        assertEquals(e.getMessage(), "Postal item with id="
                + resultPostalItem.getId() + " isn't transit");
    }

    @Test
    void shouldThrowPostOfficeNotFoundException_IfPostOfficeNotFound() {
        var addingRequest = new PostalItemAddingRequest(1, 0);
        var resultPostalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.EN_ROUTE, new LinkedHashSet<>());
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(resultPostalItem));
        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(PostOfficeNotFoundException.class,
                () -> mailService.addPostalItemToPostOffice(addingRequest));
        assertEquals(e.getMessage(), "Post office with id="
                + addingRequest.getPostOfficeId() + " not found");
    }

    @Test
    void shouldThrowPostalItemAlreadyBeenInPostOfficeException_IfPostOfficeAlreadyContains() {
        postalItem.addPostOffice(firstPostOffice);
        var addingRequest = new PostalItemAddingRequest(1, 1);
        var resultPostalItem = new PostalItem(1, MailType.LETTER, 123, "Mira 23", "Peter",
                MailStatus.EN_ROUTE, new LinkedHashSet<>());
        resultPostalItem.addPostOffice(firstPostOffice);

        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(resultPostalItem));
        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.of(firstPostOffice));

        RuntimeException e = assertThrows(PostalItemAlreadyBeenInPostOfficeException.class,
                () -> mailService.addPostalItemToPostOffice(addingRequest));
        assertEquals(e.getMessage(), "The postal item with id="
                + addingRequest.getId() + " has already been in post office with id="
                + addingRequest.getPostOfficeId());
    }


    @Test
    void leftFromPostOffice() {

    }

    @Test
    void shouldReturnPostalItemResponse_IfNotNull() {
        var postalItemResponse = new PostalItemResponse(postalItem.getMailStatus(), postalItem.getPostOffices());

        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(postalItem));
        var response = mailService.findPostalItemStatusAndStatusById(1);

        assertNotNull(response);
        assertEquals(response, postalItemResponse);
    }

    @Test
    void shouldThrowPostalItemNotFoundException_IfPostalItemNotExist() {
        int nonExistId = 0;
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(PostalItemNotFoundException.class,
                () -> mailService.findPostalItemStatusAndStatusById(nonExistId));
        assertEquals(e.getMessage(), "Post office with id=" + nonExistId + " not found");
    }

    @Test
    void changePostalItemStatusToReceived() {
    }
}