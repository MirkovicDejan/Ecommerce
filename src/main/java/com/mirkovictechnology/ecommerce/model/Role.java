package com.mirkovictechnology.ecommerce.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "roles")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull
	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	private RoleName name;
	
	@Override
	public String toString() {
		return "Role{" +
				"id=" + id +
				", name=" + name +
				'}';
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Role role = (Role) o;
		return Objects.equals(id, role.id) && name == role.name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}