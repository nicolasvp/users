package com.microservice.users.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="roles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
public class Rol implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	@NotEmpty(message="no puede estar vacío")
	@Size(min=1, max=100, message="debe tener entre 1 y 100 caracteres")
	private String name;
	
	@NotEmpty(message="no puede estar vacío")
	@Size(min=1, max=300, message="debe tener entre 1 y 300 caracteres")
	private String description;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="rol", cascade=CascadeType.ALL)
    private List<User> user;
	
	@Column(name="created_at")
	private Date createdAt;
	
	public Rol() {
		super();
	}

	public Rol(String name, String description, Date createdAt) {
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
	}

	// Set current date for createdAt field
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}
}
