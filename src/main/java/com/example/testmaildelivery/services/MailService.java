package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostalItemAddingRequest;
import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.exceptions.*;
import com.example.testmaildelivery.mappers.PostalItemMapper;
import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final PostalItemRepository postalItemRepository;
    private final PostOfficeRepository postOfficeRepository;
    private final PostalItemMapper postalItemMapper;

    public void registerPostalItem(PostalItemRegistrationRequest postalItemRegistrationRequest) {
        PostalItem postalItem = postalItemMapper.toModel(postalItemRegistrationRequest);

        PostOffice postOffice = postOfficeRepository.findById(postalItemRegistrationRequest.getPostOfficeId())
                .orElseThrow(() -> new PostNotFoundException("Post with id="
                        + postalItemRegistrationRequest.getPostOfficeId() + " not found"));

        postalItem.getPostOffices().add(postOffice);
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);
    }

    public void addPostalItemToPostOffice(PostalItemAddingRequest postalItemAddingRequest) {
        PostalItem postalItem = getPostalItemById(postalItemAddingRequest.getId());

        if (postalItem.getMailStatus() == MailStatus.RECEIVED)
            throw new PostalItemAlreadyReceivedException("The package with id="
                    + postalItemAddingRequest.getId() + "has already been received");

        if (postalItem.getMailStatus() != MailStatus.EN_ROUTE)
            throw new PostalItemNotEnRoute("Postal item with id=" + postalItemAddingRequest.getId() + " isn't transit");

        PostOffice postOffice = postOfficeRepository.findById(postalItemAddingRequest.getPostOfficeId())
                .orElseThrow(() -> new PostNotFoundException("Post with id="
                        + postalItemAddingRequest.getPostOfficeId() + " not found"));

//        Hibernate.initialize(postalItem.getPostOffices());

        log.info(postalItem.getPostOffices().toString());
        postalItem.getPostOffices().add(postOffice);
        log.info(postalItem.getPostOffices().toString());
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
    }

    public void leftFromPostOffice(long id) {
        PostalItem postalItem = getPostalItemById(id);

        if (postalItem.getMailStatus() == MailStatus.RECEIVED)
            throw new PostalItemAlreadyReceivedException("The package with id="
                    + id + " has already been received");
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotInThePostOffice("Postal item with id=" + id + " isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.EN_ROUTE);
    }

    public PostalItemResponse findPostalItemStatusAndStatusById(long id) {
        PostalItem postalItem = getPostalItemById(id);
        Hibernate.initialize(postalItem.getPostOffices());
        return new PostalItemResponse(postalItem.getPostOffices(), postalItem.getMailStatus());
    }

    public void changePostalItemStatusToReceived(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotInThePostOffice("Postal item with id=" + id + " isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.RECEIVED);
    }

    private PostalItem getPostalItemById(long id) {
        return postalItemRepository.findById(id)
                .orElseThrow(() -> new PostalItemNotFoundException("Post with id=" + id + " not found"));
    }
}
