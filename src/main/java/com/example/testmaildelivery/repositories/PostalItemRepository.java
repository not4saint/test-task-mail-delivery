package com.example.testmaildelivery.repositories;

import com.example.testmaildelivery.models.PostalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalItemRepository extends JpaRepository<PostalItem, Long> {

}
