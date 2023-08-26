package com.example.testmaildelivery.repositories;

import com.example.testmaildelivery.models.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {

//    @Query("select p1_1.id, p1_1.address, p1_1.name from post_office-postal_item p1_0 " +
//            "join fetch postoffice p1_1 on p1_1.id=p1_0.post_office_id where p1_0.postal_item_id=?1")
//    Set<PostOffice> findPostOfficesById(long id);
}
