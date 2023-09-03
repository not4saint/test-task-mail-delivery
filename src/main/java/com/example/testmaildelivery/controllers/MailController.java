package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.dto.PostalItemAddingRequest;
import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.services.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/register-postal-item")
    public ResponseEntity<HttpStatus> registerPostalItem(@RequestBody @Valid PostalItemRegistrationRequest postalItem) {
        mailService.registerPostalItem(postalItem);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/add-post-office")
    public ResponseEntity<HttpStatus> addPostalItemToPostOffice(@RequestBody PostalItemAddingRequest postalItemAddingRequest) {
        mailService.addPostalItemToPostOffice(postalItemAddingRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/left-postal-item/{id}")
    public ResponseEntity<HttpStatus> leftPostalItemFromPostOffice(@PathVariable long id) {
        mailService.leftFromPostOffice(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/check-postal-item/{id}")
    public ResponseEntity<PostalItemResponse> checkPostalItem(@PathVariable long id) {
        return ResponseEntity.ok(mailService.findPostalItemStatusAndPostOfficesById(id));
    }

    @PatchMapping("/receive-postal-item/{id}")
    public ResponseEntity<HttpStatus> receivePostalItem(@PathVariable long id) {
        mailService.changePostalItemStatusToReceived(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
