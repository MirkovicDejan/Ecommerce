package com.mirkovictechnology.ecommerce.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.mirkovictechnology.ecommerce.configuration.CustomUserDetails;
import com.mirkovictechnology.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found."));
	}
}