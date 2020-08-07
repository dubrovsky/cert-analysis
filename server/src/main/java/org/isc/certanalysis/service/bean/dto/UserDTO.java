package org.isc.certanalysis.service.bean.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
public class UserDTO {

	private Long id;
	private String login;
	private String password;
	private String firstname;
	private String lastname;
	private String surname;
	private String email;
	private String phone;
	private boolean enabled;
	private Set<Long> notificationGroupIds = new HashSet<>();
	private Long roleId;
	/*private Set<RoleDTO> roles = new HashSet<>(0);
	private Set<NotificationGroupDTO> notificationGroups = new HashSet<>(0);*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Set<Long> getNotificationGroupIds() {
		return notificationGroupIds;
	}

	public void setNotificationGroupIds(Set<Long> notificationGroupIds) {
		this.notificationGroupIds = notificationGroupIds;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	/*public Set<RoleDTO> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleDTO> roles) {
		this.roles = roles;
	}

	public Set<NotificationGroupDTO> getNotificationGroups() {
		return notificationGroups;
	}

	public void setNotificationGroups(Set<NotificationGroupDTO> notificationGroups) {
		this.notificationGroups = notificationGroups;
	}*/
}
