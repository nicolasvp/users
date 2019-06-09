package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.remote.entity.Phrase;

public interface IUserService {

	public List<User> findAll();
	
	public User findById(Long id);
	
	public User save(User user);
	
	public void delete(Long id);
	
	public String callPhraseService();
	
	public String unavailableMessage();
	
	public List<Phrase> getAllPhrases();
	
	public List<Phrase> shitHole();
}
