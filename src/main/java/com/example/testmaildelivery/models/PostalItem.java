package com.example.testmaildelivery.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table
@NoArgsConstructor
public class PostalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private MailType mailType;

    private long personIndex;

    private String address;

    private String personName;

    @Enumerated(EnumType.STRING)
    private MailStatus mailStatus;

    @ManyToMany
    @JoinTable(name = "PostOffice - PostalItem",
            joinColumns = @JoinColumn(name = "postal_item_id"),
            inverseJoinColumns = @JoinColumn(name = "post_office_id"))
    private Set<PostOffice> postOffices;
}
