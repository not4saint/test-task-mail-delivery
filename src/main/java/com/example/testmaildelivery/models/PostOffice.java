package com.example.testmaildelivery.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(exclude="postalItems")
@Entity @Table(name = "postoffice")
@NoArgsConstructor
@AllArgsConstructor
public class PostOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String address;

    @ManyToMany(mappedBy = "postOffices", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JsonBackReference
    @JsonIgnore
    private Set<PostalItem> postalItems = new LinkedHashSet<>();

//    public Set<PostalItem> getPostalItems() {
//        if (this.postalItems == null)
//            this.postalItems = new HashSet<>();
//        return postalItems;
//    }

    @Override
    public String toString() {
        return "PostOffice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
