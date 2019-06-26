package com.microservice.users.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.microservice.users.models.services.IUtilService;
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

import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IHistoryService;
import com.microservice.users.models.services.IUserService;
import com.microservice.users.models.services.remote.entity.Phrase;

@RestController
@RequestMapping("/api")
public class UserController {

	protected Logger LOGGER = Logger.getLogger(UserController.class.getName());
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IHistoryService historyService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> index(){
		return userService.findAll();
	}

	@GetMapping(path="/users/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public String users(){
		return userService.callPhraseService();
	}
	
	@GetMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		User user = null;
		Map<String, Object> response = new HashMap<>();

		try {
			user = userService.findById(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (user == null) {
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@PostMapping(path="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
		
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
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("user", newUser);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable("id") Long id) {
		
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
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			userFromDB.setName(user.getName());
			userFromDB.setLastName(user.getLastName());
			userFromDB.setEmail(user.getEmail());
			userFromDB.setPassword(user.getPassword());
			userUpdated = userService.save(userFromDB);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("user", userUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			userService.delete(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar eliminar el registro en la base de datos, el registro no existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Pick a random phrase for every user based on his config phrase type and assigned to his history
	 */
	@GetMapping(path="/users/set-phrases-to-users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setPhrasesToUsers() throws NoSuchAlgorithmException {
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
					response.put("msg", "Error al intentar guardar el registro");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
			}
		}
		
		response.put("phrasesAsigned", phrasesAsignedToUsers);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
