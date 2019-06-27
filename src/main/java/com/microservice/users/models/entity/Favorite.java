package com.microservice.users.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="user_favorities")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Favorite implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	@NotNull(message="no puede estar vacío")
	private User user;

	@Column(name="phrase_id")
	@NotNull(message="no puede estar vacío")
	private Long phraseId;
	
	@Column(name="created_at")
	private Date createdAt;
	
	public Favorite() {
		super();
	}

	public Favorite(User user, Long phraseId, Date createdAt) {
		this.user = user;
		this.phraseId = phraseId;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getPhraseId() {
		return phraseId;
	}

	public void setPhraseId(Long phraseId) {
		this.phraseId = phraseId;
	}	
}
