package com.mirkovictechnology.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirkovictechnology.ecommerce.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Page<Item> findByCategory(String category, Pageable pageable);
}
