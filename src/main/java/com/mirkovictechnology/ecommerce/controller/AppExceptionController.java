package com.mirkovictechnology.ecommerce.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import com.mirkovictechnology.ecommerce.exception.AppError;
import com.mirkovictechnology.ecommerce.exception.ItemAdminException;
import com.mirkovictechnology.ecommerce.exception.ItemShopException;
import com.mirkovictechnology.ecommerce.exception. RegistrationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class AppExceptionController {
	
	private static final String RETURN_ERROR_LOG = "Returning error {} with status {}";
	
	@ExceptionHandler(value = { RegistrationException.class})
	public String handleRegistrationException ( RegistrationException ex, Model model) {
		AppError error = new AppError();
		log.debug("An RegistrationException occurred: {}", ex.toString());
		error.setMessage(ex.getMessage());
		log.debug(RETURN_ERROR_LOG, error, ex.getStatus());
		model.addAttribute("errorMessage", error.getMessage());
		model.addAttribute("errorTime", error.getTimestamp());
		return "register.html";
	}
	
	@ExceptionHandler(value = { ItemAdminException.class})
	public String handleItemAdminException( ItemAdminException ex, Model model) {
		AppError error = new AppError();
		log.debug("An ItemAdminException occurred: {}", ex.toString());
		error.setMessage(ex.getMessage());
		log.debug(RETURN_ERROR_LOG, error, ex.getStatus());
		model.addAttribute("errorMessage", error.getMessage());
		model.addAttribute("errorTime", error.getTimestamp());
		return "admin-products.html";
	}
	
	@ExceptionHandler(value = { ItemShopException.class})
	public String handleItemShopException( ItemShopException ex, Model model) {
		AppError error = new AppError();
		log.debug("An ItemShopException occurred: {}", ex.toString());
		error.setMessage(ex.getMessage());
		log.debug(RETURN_ERROR_LOG, error, ex.getStatus());
		model.addAttribute("errorMessage", error.getMessage());
		model.addAttribute("errorTime", error.getTimestamp());
		return "shop.html";
	}
}