package br.com.datahub.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;

	@Email(message = "Please provide a valid email address")
	@NotBlank(message = "Email is mandatory")
	@Column(unique = true, length = 250)
	private String email;
	private String password;

	@Column(name = "ldap_user", columnDefinition = "boolean default false")
	private boolean ldapUser;

	@OneToOne
	@JoinColumn(name = "fk_id_role")
	private Role role;

	public User() {
	}
	
	public User(User other) {
		this.id = other.id;
		this.name = other.name;
		this.email = other.email;
		this.password = other.password;
		this.ldapUser = other.ldapUser;
		this.role = other.role;
	}

	public void setPassword(String password) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		this.password = encodedPassword;
	}
	
	public void setEncodedPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(boolean ldapUser) {
		this.ldapUser = ldapUser;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name);
	}

}
