package com.example.testmaildelivery.services;

import com.example.testmaildelivery.dto.PostalItemRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.exceptions.PostalItemNotEnRoute;
import com.example.testmaildelivery.exceptions.PostalItemNotFoundException;
import com.example.testmaildelivery.exceptions.PostalItemNotInThePostOffice;
import com.example.testmaildelivery.models.MailStatus;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.repositories.PostalItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final PostalItemRepository postalItemRepository;

    public void addPostalItem(PostalItem postalItem) {
        postalItemRepository.save(postalItem);
    }

    public void addPostOffice(PostalItemRequest postalItemRequest) {
        PostalItem postalItem = getPostalItemById(postalItemRequest.getId());
        if (postalItem.getMailStatus() != MailStatus.EN_ROUTE) {
            throw new PostalItemNotEnRoute("Postal item isn't transit");
        }

        postalItem.getPostOffices().add(postalItemRequest.getPostOffice());
        postalItem.setMailStatus(MailStatus.IN_THE_POST_OFFICE_FOR_SORTING);
        postalItemRepository.save(postalItem);
    }

    public void leftFromPostOffice(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() != MailStatus.IN_THE_POST_OFFICE_FOR_SORTING) {
            throw new PostalItemNotInThePostOffice("Postal item isn't in the post office");
        }

        postalItem.setMailStatus(MailStatus.EN_ROUTE);
        postalItemRepository.save(postalItem);
    }

    public PostalItemResponse findPostalItemStatusAndRouteById(long id) {
        PostalItem postalItem = getPostalItemById(id);
        return new PostalItemResponse(postalItem.getPostOffices(), postalItem.getMailStatus());
    }

    public void changePostalItemStatus(long id) {
        PostalItem postalItem = getPostalItemById(id);
        if (postalItem.getMailStatus() != MailStatus.EN_ROUTE) {
            throw new PostalItemNotEnRoute("Postal item isn't transit");
        }

        postalItem.setMailStatus(MailStatus.RECEIVED);
        postalItemRepository.save(postalItem);
    }

    private PostalItem getPostalItemById(long id) {
        Optional<PostalItem> postalItemOptional = postalItemRepository.findById(id);
        if (postalItemOptional.isEmpty()) {
            throw new PostalItemNotFoundException("Postal item not found");
        } else {
            return postalItemOptional.get();
        }
    }
}
