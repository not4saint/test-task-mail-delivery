package com.example.testmaildelivery.repositories;

import com.example.testmaildelivery.models.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {

//    @Query(value = "select po.id, po.name, po.address from PostOffice po " +
//            "where po.id in (select post_office_id from post_office_postal_item where postal_item_id = ?1)", nativeQuery = true)
    @Query(value = "select po.id, po.name, po.address from PostOffice po " +
            "join post_office_postal_item popi on po.id = popi.post_office_id where popi.postal_item_id = ?1", nativeQuery = true)
    Set<PostOffice> findPostOfficesById(long id);
}
