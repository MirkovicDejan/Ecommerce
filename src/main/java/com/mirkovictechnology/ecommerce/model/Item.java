package com.mirkovictechnology.ecommerce.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "price")
	private BigDecimal price;
	
	@NotNull
	@Column(name = "category")
	private String category;
	
	@NotNull
	@Column(name = "image",columnDefinition = "bytea")
	private byte[] image;
	
	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", price=" + price +
				", category='" + category + '\'' +
				", image=" + Arrays.toString(image) +
				'}';
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Item item = (Item) o;
		return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(price, item.price) && Objects.equals(category, item.category) && Objects.deepEquals(image, item.image);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, price, category, Arrays.hashCode(image));
	}
}