package com.mirkovictechnology.ecommerce.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mirkovictechnology.ecommerce.exception.RegistrationException;
import com.mirkovictechnology.ecommerce.model.Role;
import com.mirkovictechnology.ecommerce.model.RoleName;
import com.mirkovictechnology.ecommerce.model.User;
import com.mirkovictechnology.ecommerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	@Value("${admin-email}")
	private String adminEmail;
	
	@Value("${admin-password}")
	private String adminPassword;
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@PostConstruct
	public void iniAdmin(){
		if (!userRepository.existsByEmail(adminEmail)){
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setEmail("admin@email.com");
			admin.setRoles(new HashSet<>(List.of(new Role(1L,RoleName.USER),new Role(2L,RoleName.ADMIN))));
			userRepository.save(admin);
			log.info("Admin created.");
		}
		else {
		    log.info("Admin exists.");
		}
	}
	
	public void registerNewUser(final User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			log.debug("User with email {} already exists.", user.getEmail());
			throw new RegistrationException("User with email: "+user.getEmail()+" already exists.", HttpStatus.BAD_REQUEST);
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(new HashSet<>(List.of(new Role(1L, RoleName.USER))));
		userRepository.save(user);
	}
	
	public User findByUsername(final String username) {
		return userRepository.findByUsername(username);
	}
}