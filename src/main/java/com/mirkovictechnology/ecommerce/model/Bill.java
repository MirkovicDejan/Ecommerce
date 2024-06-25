package com.mirkovictechnology.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bill")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Bill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BillItems> billItems;
	
	@NotNull
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@NotNull
	@Column(name = "creation_date_time")
	private LocalDateTime createdDateTime;
	
	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Bill bill = (Bill) o;
		return Objects.equals(id, bill.id) && Objects.equals(user, bill.user) && Objects.equals(billItems, bill.billItems) && Objects.equals(totalAmount, bill.totalAmount) && Objects.equals(createdDateTime, bill.createdDateTime);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, user, billItems, totalAmount, createdDateTime);
	}
	
	@Override
	public String toString() {
		return "Bill{" +
				"id=" + id +
				", totalAmount=" + totalAmount +
				", createdDateTime=" + createdDateTime +
				'}';
	}
}