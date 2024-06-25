package com.mirkovictechnology.ecommerce.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.mirkovictechnology.ecommerce.exception.CustomAuthenticationFailureHandler;
import com.mirkovictechnology.ecommerce.model.RoleName;
import com.mirkovictechnology.ecommerce.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomUserDetailsService userDetailsService;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeHttpRequests(request -> request.requestMatchers("/", "/static/css/**", "/static/js/**", "/static/img/**", "/register", "/login").permitAll()
						.requestMatchers("/admin/**").hasRole(RoleName.ADMIN.name())
						.requestMatchers("/shop").hasRole(RoleName.USER.name())
						.anyRequest().authenticated())
				.formLogin(f -> f.loginPage("/login")
						.usernameParameter("email")
						.permitAll()
						.defaultSuccessUrl("/process", true)
						.failureUrl("/login?error=true")
						.failureHandler(customAuthenticationFailureHandler))
				.logout(logoutConfigurer ->
						logoutConfigurer.clearAuthentication(true)
								.invalidateHttpSession(true)
								.logoutSuccessUrl("/")
								.logoutRequestMatcher(new AntPathRequestMatcher("/logout")));
		
		return httpSecurity.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}