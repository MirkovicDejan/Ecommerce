package com.mirkovictechnology.ecommerce.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull
	@Column(name = "username")
	private String username;
	
	@NotNull
	@Column(name = "password")
	private String password;
	
	@NotNull
	@Column(name = "email")
	private String email;
	
	@NotNull
	@Column(name = "enabled")
	private boolean enabled = true;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
	private Set<Role> roles = new HashSet<>();
	
	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", enabled=" + enabled +
				", roles=" + roles +
				'}';
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final User user = (User) o;
		return enabled == user.enabled && Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(roles, user.roles);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, username, email, enabled, roles);
	}
}