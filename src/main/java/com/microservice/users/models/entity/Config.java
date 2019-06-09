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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="user_config")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
public class Config implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="no puede estar vacío")
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="language_id", referencedColumnName="id")
	private Language language;
	
	@Column(name="phrase_type")
	private int phraseType;
	
	@Column(name="activate_plugin")
	private boolean activatePlugin;
	
	@Column(name="created_at")
	private Date createdAt;
	
	public Config() {
		super();
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

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Integer getPhraseType() {
		return phraseType;
	}

	public void setPhraseType(int phraseType) {
		this.phraseType = phraseType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Boolean isActivatePlugin() {
		return activatePlugin;
	}

	public void setActivatePlugin(boolean activatePlugin) {
		this.activatePlugin = activatePlugin;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
