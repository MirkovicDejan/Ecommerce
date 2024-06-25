package com.mirkovictechnology.ecommerce.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ItemShopException extends AppException{
	public ItemShopException(final String message, final HttpStatus status) {
		super(message, status);
	}
}