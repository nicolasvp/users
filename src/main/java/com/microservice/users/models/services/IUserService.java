package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.remote.entity.Phrase;

public interface IUserService {

	public List<User> findAll();
	
	public User findById(Long id);
	
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
