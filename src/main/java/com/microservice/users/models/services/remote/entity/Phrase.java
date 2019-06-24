package com.microservice.users.models.services.remote.entity;


public class Phrase {

	private Long id;
	
	private String body;
	
	private Long likesCounter;
	
	private Type type;
	
	private Author author;
	
	private Image image;

	public Phrase(){
		super();
	}

	public Phrase(Long id, String body, Long likesCounter, Type type, Author author, Image image){
		this.id = id;
		this.body = body;
		this.likesCounter = likesCounter;
		this.type = type;
		this.author = author;
		this.image = image;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public Long getLikesCounter() {
		return likesCounter;
	}
	
	public void setLikesCounter(Long likesCounter) {
		this.likesCounter = likesCounter;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
