package com.example.testmaildelivery.controllers;

import com.example.testmaildelivery.dto.PostalItemRequest;
import com.example.testmaildelivery.dto.PostalItemResponse;
import com.example.testmaildelivery.models.PostalItem;
import com.example.testmaildelivery.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/register-postal-item")
    public ResponseEntity<HttpStatus> registerPostalItem(@RequestBody PostalItem postalItem) {
        mailService.addPostalItem(postalItem);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/add-postal-item")
    public ResponseEntity<HttpStatus> addPostalItem(@RequestBody PostalItemRequest postalItemRequest) {
        mailService.addPostOffice(postalItemRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/left-postal-item/{id}")
    public ResponseEntity<HttpStatus> leftPostalItem(@PathVariable long id) {
        mailService.leftFromPostOffice(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/check-postal-item/{id}")
    public PostalItemResponse checkPostalItem(@PathVariable long id) {
        return mailService.findPostalItemStatusAndRouteById(id);
    }

    @PostMapping("/receive-postal-item/{id}")
    public ResponseEntity<HttpStatus> receivePostalItem(@PathVariable long id) {
        mailService.changePostalItemStatus(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
