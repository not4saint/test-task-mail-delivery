package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostalItemAddingRequest;
import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.exceptions.PostNotFoundException;
import com.example.testmaildelivery.exceptions.PostalItemNotEnRoute;
import com.example.testmaildelivery.exceptions.PostalItemNotFoundException;
import com.example.testmaildelivery.exceptions.PostalItemNotInThePostOffice;
import com.example.testmaildelivery.mappers.PostalItemMapper;
import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.PostOffice;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostOfficeRepository;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                .orElseThrow(() -> new PostNotFoundException("Post with current id not found"));

        postalItem.getPostOffices().add(postOffice);
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
        postalItemRepository.save(postalItem);
    }

    public void addPostalItemToPostOffice(PostalItemAddingRequest postalItemAddingRequest) {
        PostalItem postalItem = getPostalItemById(postalItemAddingRequest.getId());
        if (postalItem.getMailStatus() != MailStatus.EN_ROUTE) {
            throw new PostalItemNotEnRoute("Postal item isn't transit");
        }
//        Hibernate.initialize(postalItem);
        PostOffice postOffice = postOfficeRepository.findById(postalItemAddingRequest.getPostOfficeId())
                .orElseThrow(() -> new PostNotFoundException("Post with current id not found"));

        postalItem.getPostOffices().add(postOffice);
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE);
    }

    public void leftFromPostOffice(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotInThePostOffice("Postal item isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.EN_ROUTE);
    }

    public PostalItemResponse findPostalItemStatusAndStatusById(long id) {
        PostalItem postalItem = getPostalItemById(id);
        return new PostalItemResponse(postalItem.getPostOffices(), postalItem.getMailStatus());
    }

    public void changePostalItemStatusToReceived(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE) {
            throw new PostalItemNotEnRoute("Postal item isn't transit");
        }

        postalItem.setMailStatus(MailStatus.RECEIVED);
    }

    private PostalItem getPostalItemById(long id) {
        return postalItemRepository.findById(id)
                .orElseThrow(() -> new PostalItemNotFoundException("Postal item not found"));
    }
}
