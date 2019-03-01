package org.isc.certanalysis.domain;
// Generated Jan 25, 2019 9:50:29 AM by Hibernate Tools 4.3.5.Final

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Users generated by hbm2java
 */
@Entity
@Table(name = "USERS", schema = "CERT_REP3", uniqueConstraints = @UniqueConstraint(columnNames = "LOGIN"))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity {

	private long id;
	private String login;
	private String password;
	private String firstname;
	private String lastname;
	private String surname;
	private String email;
	private String phone;
	private boolean enabled;
	private Set<Role> roles = new HashSet<Role>(0);
	private Set<NotificationGroup> notificationGroups = new HashSet<NotificationGroup>(0);

	public User() {
	}

	public User(long id, String login, String password, String firstname, String lastname, String email,
	            boolean enabled, String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.login = login;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.enabled = enabled;
	}

	public User(long id, String login, String password, String firstname, String lastname, String surname,
	            String email, String phone, boolean enabled, String createdBy, Instant createdDate,
	            String lastModifiedBy, Instant lastModifiedDate, Set<Role> roles,
	            Set<NotificationGroup> notificationGroups) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.login = login;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.surname = surname;
		this.email = email;
		this.phone = phone;
		this.enabled = enabled;
		this.roles = roles;
		this.notificationGroups = notificationGroups;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ")
	@SequenceGenerator(sequenceName = "SEQ_USERS", name = "USERS_SEQ")
	@Column(name = "ID", unique = true, nullable = false, precision = 20, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "LOGIN", unique = true, nullable = false, length = 24)
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "PASSWORD", nullable = false, length = 24)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "FIRSTNAME", nullable = false, length = 24)
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "LASTNAME", nullable = false, length = 24)
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "SURNAME", length = 24)
	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Column(name = "EMAIL", nullable = false, length = 24)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "PHONE", length = 24)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "ENABLED", nullable = false, precision = 1, scale = 0)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE})
	@JoinTable(name = "USERS_ROLE", schema = "CERT_REP3", joinColumns = {
			@JoinColumn(name = "USER_ID", nullable = false, updatable = false)}, inverseJoinColumns = {
			@JoinColumn(name = "ROLE_ID", nullable = false, updatable = false)})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		roles.add(role);
		role.getUsers().add(this);
	}

	public void removeRole(Role role) {
		roles.remove(role);
		role.getUsers().remove(this);
	}

	public void removeRoles() {
		for(Role role : new ArrayList<>(roles)) {
			removeRole(role);
		}
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE})
	@JoinTable(name = "USERS_NOTIFICATION_GROUP", schema = "CERT_REP3", joinColumns = {
			@JoinColumn(name = "USER_ID", nullable = false, updatable = false)}, inverseJoinColumns = {
			@JoinColumn(name = "NOTIFICATION_GROUP_ID", nullable = false, updatable = false)})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<NotificationGroup> getNotificationGroups() {
		return this.notificationGroups;
	}

	public void setNotificationGroups(Set<NotificationGroup> notificationGroups) {
		this.notificationGroups = notificationGroups;
	}

	public void addNotificationGroup(NotificationGroup notificationGroup) {
		notificationGroups.add(notificationGroup);
		notificationGroup.getUsers().add(this);
	}

	public void removeNotificationGroup(NotificationGroup notificationGroup) {
		notificationGroups.remove(notificationGroup);
		notificationGroup.getUsers().remove(this);
	}

	public void removeNotificationGroups() {
		for(NotificationGroup notificationGroup : new ArrayList<>(notificationGroups)) {
			removeNotificationGroup(notificationGroup);
		}
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		return getId() != null && getId().equals(((User) o).getId());
	}
}
