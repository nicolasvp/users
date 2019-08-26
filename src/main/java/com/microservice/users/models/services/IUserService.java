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
	 * Filter the phrase list based on the user phrase type
	 */
	public  List<Phrase> filterPhrasesByType(List<Phrase> allPhrases, Integer phraseType);
	
	/*
	 * Filter the phrase list based on the user history list, if the user has the phrase on this history then its removed from the "allPhrases" list
	 */
	public List<Phrase> filterPhraseByAvailability(List<Phrase> allPhrases, List<History> userHistory);
	
	/*
	 * Remote calls methods
	 */
	public String callPhraseService();
	
	public String unavailableMessage();
	
	public List<Phrase> getAllPhrases();
	
	public List<Phrase> getAllPhrasesFail();
}
