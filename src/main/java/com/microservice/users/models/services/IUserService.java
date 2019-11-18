package com.microservice.users.models.services;

import java.util.List;
import com.microservices.commons.models.entity.delivery.History;
import com.microservices.commons.models.entity.users.User;
import com.microservices.commons.models.entity.phrases.Phrase;

public interface IUserService {

	public List<User> findAll();
	
	public User findById(Long id);
	
	public User findByUsername(String username);
	
	public User save(User user);
	
	public void delete(Long id);
	
	/*
	 * Remote calls methods
	 */
	public String callPhraseService();
	
	public String unavailableMessage();
	
	public List<Phrase> getAllPhrases();
	
	public List<Phrase> getAllPhrasesFail();
}
