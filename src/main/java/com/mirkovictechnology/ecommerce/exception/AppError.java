package com.mirkovictechnology.ecommerce.exception;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AppError {
	private LocalDateTime timestamp = LocalDateTime.now();
	private String message;
}