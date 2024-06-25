package com.mirkovictechnology.ecommerce.configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.mirkovictechnology.ecommerce.model.User;
import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {
	
	private String userName;
	private String email;
	private String password;
	private boolean isEnabled;
	private List<GrantedAuthority> authorities;
	
	public CustomUserDetails(User user) {
		this.userName = user.getUsername();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.isEnabled = user.isEnabled();
		this.authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
				.collect(Collectors.toList());
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
}