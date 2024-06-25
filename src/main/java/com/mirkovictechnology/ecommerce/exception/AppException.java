package com.mirkovictechnology.ecommerce.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class AppException extends RuntimeException {
	
	private final HttpStatus status;
	
	public AppException(String message,HttpStatus status){
		super(message);
		this.status=status;
	}
}