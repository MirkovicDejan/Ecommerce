package com.mirkovictechnology.ecommerce.model;

import java.math.BigDecimal;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bill_items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BillItems {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "bill_id", nullable = false)
	private Bill bill;
	
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	
	@NotNull
	@Column(name = "quantity")
	private Integer quantity;
	
	@NotNull
	@Column(name = "item_price")
	private BigDecimal itemPrice;
	
	
	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final BillItems billItem = (BillItems) o;
		return Objects.equals(id, billItem.id) && Objects.equals(bill, billItem.bill) && Objects.equals(item, billItem.item) && Objects.equals(quantity, billItem.quantity) && Objects.equals(itemPrice, billItem.itemPrice);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, bill, item, quantity, itemPrice);
	}
}