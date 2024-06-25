package com.mirkovictechnology.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirkovictechnology.ecommerce.model.Bill;

@Repository
public interface BillRepository  extends JpaRepository<Bill, Long> {
}