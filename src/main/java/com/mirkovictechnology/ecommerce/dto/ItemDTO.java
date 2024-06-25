package com.mirkovictechnology.ecommerce.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ItemDTO {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private String category;
	private MultipartFile image;
	private String base64Image;
}