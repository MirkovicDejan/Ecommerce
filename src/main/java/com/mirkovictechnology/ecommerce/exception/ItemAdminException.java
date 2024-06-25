package com.mirkovictechnology.ecommerce.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ItemAdminException extends AppException{
	public ItemAdminException(final String message, final HttpStatus status) {
		super(message, status);
	}
}