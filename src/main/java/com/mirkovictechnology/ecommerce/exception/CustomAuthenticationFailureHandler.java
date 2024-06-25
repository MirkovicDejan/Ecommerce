package com.mirkovictechnology.ecommerce.exception;

import java.io.IOException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof BadCredentialsException) {
			request.getSession().setAttribute("errorMessage", "Invalid email or password.");
			log.error("Error while logging in. Reason: {}", exception.getMessage());
		}
		response.sendRedirect("/login?error=true");
	}
}