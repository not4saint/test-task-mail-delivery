package com.example.testmaildelivery.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "postalitem")
@NoArgsConstructor
@AllArgsConstructor
public class PostalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "mail_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MailType mailType;

    @Column(name = "person_index", nullable = false)
    private long personIndex;

    @NotNull
    private String address;

    @Column(name = "person_name", nullable = false)
    private String personName;

    @Enumerated(EnumType.STRING)
    @Column(name = "mail_status")
    private MailStatus mailStatus;

    @ManyToMany()
    @JoinTable(name = "post_office-postal_item",
            joinColumns = @JoinColumn(name = "postal_item_id"),
            inverseJoinColumns = @JoinColumn(name = "post_office_id"))
    private Set<PostOffice> postOffices = new HashSet<>();

//    public Set<PostOffice> getPostOffices() {
//        if (this.postOffices == null)
//            this.postOffices = new HashSet<>();
//        return postOffices;
//    }

    @Override
    public String toString() {
        return "PostalItem{" +
                "id=" + id +
                ", mailType=" + mailType +
                ", personIndex=" + personIndex +
                ", address='" + address + '\'' +
                ", personName='" + personName + '\'' +
                ", mailStatus=" + mailStatus +
                '}';
    }
}
