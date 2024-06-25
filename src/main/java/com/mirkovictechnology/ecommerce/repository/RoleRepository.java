package com.mirkovictechnology.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirkovictechnology.ecommerce.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
