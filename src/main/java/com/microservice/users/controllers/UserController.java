package com.microservice.users.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;

import com.microservice.users.models.services.IUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.users.config.MessagesTranslate;
import com.microservice.users.enums.DatabaseMessagesEnum;
import com.microservice.users.exceptions.DatabaseAccessException;
import com.microservice.users.exceptions.NullRecordException;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IHistoryService;
import com.microservice.users.models.services.IUserService;
import com.microservice.users.models.services.remote.entity.Phrase;

@RestController
@RequestMapping("/api")
public class UserController {

	protected Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IHistoryService historyService;

	@Autowired
	private IUtilService utilService;

	@Autowired
	private MessagesTranslate messages;
	
	@GetMapping(path="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> index(){
		return userService.findAll();
	}

	@GetMapping(path="/users/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public String users(){
		return userService.callPhraseService();
	}
	
	@GetMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		User user = null;

		try {
			user = userService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		if (user == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@PostMapping(path="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) throws DatabaseAccessException {
		
		User newUser = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newUser = userService.save(user);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getCreated());
		response.put("user", newUser);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		User userFromDB = userService.findById(id);
		User userUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (userFromDB == null) {
			throw new NullRecordException();
		}

		try {
			userFromDB.setName(user.getName());
			userFromDB.setLastName(user.getLastName());
			userFromDB.setEmail(user.getEmail());
			userFromDB.setPassword(user.getPassword());
			userUpdated = userService.save(userFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getUpdated());
		response.put("user", userUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			userService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getDeleted());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Pick a random phrase for every user based on his config phrase type and assigned to his history
	 */
	@GetMapping(path="/users/set-phrases-to-users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException {
		Map<String, Object> response = new HashMap<>();
		List<User> allUsers = userService.findAll();
		List<Phrase> allPhrases = userService.getAllPhrases();
		Map<String, String> phrasesAsignedToUsers = new HashMap<>();
		Random randomElement = SecureRandom.getInstanceStrong();  
		
		for(User user: allUsers) {
			History newUserHistory = new History();
			Integer userPhraseType = user.getConfig().getPhraseType();
			List<History> userHistory = user.getHistory();
			List<Phrase> filteredPhrases = userService.filterPhrasesByType(allPhrases, userPhraseType);
			List<Phrase> availablePhrasesForUser = userService.filterPhraseByAvailability(filteredPhrases, userHistory);
			
			if(!availablePhrasesForUser.isEmpty()) {
				Phrase randomPhraseSelected = availablePhrasesForUser.get(randomElement.nextInt(availablePhrasesForUser.size()));
				
				try {
					newUserHistory.setPhraseId(randomPhraseSelected.getId());
					newUserHistory.setUser(user);
					historyService.save(newUserHistory);
				} catch (DataAccessException e) {
					throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
				}

				phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
			}
		}
		
		response.put("phrasesAsigned", phrasesAsignedToUsers);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
