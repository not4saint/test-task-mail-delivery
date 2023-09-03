package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostalItemAddingRequest;
import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.exceptions.PostNotFoundException;
import com.example.testmaildelivery.exceptions.PostalItemNotFoundException;
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
import java.util.Set;

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
                MailStatus.IN_THE_POST_OFFICE, Set.of(firstPostOffice));
    }

    @Test
    void shouldReturnPostalItem_AfterRegister() {
        var registrationRequest = new PostalItemRegistrationRequest(MailType.LETTER,
                                                                    123,
                                                                    "Mira 23",
                                                                    "Peter",
                                                                    1);

        when(postalItemMapper.toModel(any(PostalItemRegistrationRequest.class)))
                            .thenReturn(PostalItem.builder()
                                                            .id(1)
                                                            .mailType(MailType.LETTER)
                                                            .personIndex(123)
                                                            .address("Mira 23")
                                                            .personName("Peter")
                                                            .postOffices(new LinkedHashSet<>())
                                                            .build());

        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.of(firstPostOffice));
        when(postalItemRepository.save(any(PostalItem.class))).thenAnswer(i -> i.getArguments()[0]);

        PostalItem resultPostalItem = mailService.registerPostalItem(registrationRequest);

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
        assertThrows(PostNotFoundException.class, () -> mailService.registerPostalItem(registrationRequest));
    }

    @Test
    void shouldAddPostalItemToPostOffice_AfterRequest() {
        postalItem.setMailStatus(MailStatus.EN_ROUTE);

        var addingRequest = new PostalItemAddingRequest(1L, 2L);
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.of(postalItem));
        when(postOfficeRepository.findById(any(Long.class))).thenReturn(Optional.of(secondPostOffice));


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
        when(postalItemRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(PostalItemNotFoundException.class, () -> mailService.findPostalItemStatusAndStatusById(0));
    }

    @Test
    void changePostalItemStatusToReceived() {
    }
}