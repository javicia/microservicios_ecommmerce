package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "users")
public class User {

	@Id
	private String id;

	private String username;

	private String name;

	private String surname;

	private String dni;

	private String address;

	private String city;

	private String zipCode;

	private String email;

	@JsonIgnore
	private String password;

	private String role;

	public String getId() {
		return id;
	}

	public User() {
	}

	public User(String username, String name, String surname, String dni, String address, String city, String zipCode,
			String email, String password, String role) {
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.dni = dni;
		this.address = address;
		this.city = city;
		this.zipCode = zipCode;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	// Getter y Setter

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
