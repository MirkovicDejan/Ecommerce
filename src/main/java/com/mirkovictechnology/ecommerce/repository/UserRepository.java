package com.mirkovictechnology.ecommerce.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirkovictechnology.ecommerce.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(final String email);
	
	Boolean existsByEmail(final String email);
	
	User findByUsername(final String username);
}
