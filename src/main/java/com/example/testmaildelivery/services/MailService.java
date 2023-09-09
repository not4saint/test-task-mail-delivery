package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostOfficeAddingRequest;
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
                .orElseThrow(() -> new PostOfficeNotFoundException("Post office with id="
                        + postalItemRegistrationRequest.getPostOfficeId() + " not found"));

        postalItem.addPostOffice(postOffice);
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);
    }

    public void addPostalItemToPostOffice(PostOfficeAddingRequest postOfficeAddingRequest) {
        PostalItem postalItem = getPostalItemById(postOfficeAddingRequest.getId());

        if (postalItem.getMailStatus() == MailStatus.RECEIVED)
            throw new PostalItemAlreadyReceivedException("The postal item with id="
                    + postOfficeAddingRequest.getId() + " has already been received");

        if (postalItem.getMailStatus() != MailStatus.EN_ROUTE)
            throw new PostalItemNotEnRouteException("Postal item with id=" + postOfficeAddingRequest.getId() + " isn't transit");

        PostOffice postOffice = postOfficeRepository.findById(postOfficeAddingRequest.getPostOfficeId())
                .orElseThrow(() -> new PostOfficeNotFoundException("Post office with id="
                        + postOfficeAddingRequest.getPostOfficeId() + " not found"));

        Hibernate.initialize(postalItem.getPostOffices());
        if (postalItem.getPostOffices().contains(postOffice)) {
            throw new PostalItemAlreadyBeenInPostOfficeException("The postal item with id="
                    + postOfficeAddingRequest.getId() + " has already been in post office with id="
                    + postOfficeAddingRequest.getPostOfficeId());
        }

        postalItem.addPostOffice(postOffice);
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
    }

    public void leftFromPostOffice(long id) {
        PostalItem postalItem = getPostalItemById(id);

        if (postalItem.getMailStatus() == MailStatus.RECEIVED)
            throw new PostalItemAlreadyReceivedException("The postal item with id="
                    + id + " has already been received");
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotInThePostOfficeException("Postal item with id=" + id + " isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.EN_ROUTE);
    }

    public PostalItemResponse findPostalItemStatusAndPostOfficesById(long id) {
        PostalItem postalItem = getPostalItemById(id);
        return new PostalItemResponse(postalItem.getMailStatus(), postalItem.getPostOffices());
    }

    public void changePostalItemStatusToReceived(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() == MailStatus.RECEIVED)
            throw new PostalItemAlreadyReceivedException("The postal item with id="
                    + id + " has already been received");

        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotInThePostOfficeException("Postal item with id=" + id + " isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.RECEIVED);
    }

    private PostalItem getPostalItemById(long id) {
        return postalItemRepository.findById(id)
                .orElseThrow(() -> new PostalItemNotFoundException("Postal item with id=" + id + " not found"));
    }
}
