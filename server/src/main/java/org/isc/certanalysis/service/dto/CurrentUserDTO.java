package org.isc.certanalysis.service.dto;

import org.isc.certanalysis.domain.User;

import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
public class CurrentUserDTO {

	private long id;
	private String login;
	private String firstname;
	private String lastname;
	private String surname;
	private String email;
	private String phone;
	private boolean enabled;
	private Set<String> authorities = new HashSet<>();

	public CurrentUserDTO() {
	}

	public CurrentUserDTO(User user) {
		this.id = user.getId();
		this.login = user.getLogin();
		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.surname = user.getSurname();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.enabled = user.isEnabled();
		user.getRoles().forEach(role -> {
			this.authorities.add(role.getName());
			role.getPrivileges().forEach(privilege -> this.authorities.add(privilege.getName()));
		});
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String name) {
		this.login = login;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}
}
