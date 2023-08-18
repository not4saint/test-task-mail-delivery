package com.example.testmaildelivery.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity @Table
@NoArgsConstructor
public class PostOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String address;

    @ManyToMany
    private Set<PostalItem> postalItems;
}
