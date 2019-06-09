package com.microservice.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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

import com.microservice.users.models.entity.Favorite;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IFavoriteService;
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
	
	@GetMapping("/users")
	public List<User> index(){
		return userService.findAll();
	}
	
	@GetMapping("/service-route")
	public String serviceRoute() {
		return "Hi from users service";
	}
	
	@GetMapping("/users/phrases")
	public String users(){
		return userService.callPhraseService();
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		User user = null;
		Map<String, Object> response = new HashMap<>();

		try {
			user = userService.findById(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		if (user == null) {
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@PostMapping("/users")
	public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
		
		User newUser = null;
		Map<String, Object> response = new HashMap<>();

		// Si no pasa la validación entonces lista los errores y los retorna
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newUser = userService.save(user);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar guardar el registro");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("user", newUser);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/users/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable("id") Long id) {
		
		User userFromDB = userService.findById(id);
		User userUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// Si no pasa la validación entonces lista los errores y los retorna
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Si no se encontró el registro devuelve un error
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
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("user", userUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			userService.delete(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar eliminar el registro en la base de datos, el registro no existe");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Pick a random phrase for every user based on his config phrase type and asigned to his history
	 */
	@GetMapping("/users/set-phrases-to-users")
	public ResponseEntity<?> setPhrasesToUsers() {
		Map<String, Object> response = new HashMap<>();
		List<User> allUsers = userService.findAll();
		List<Phrase> allPhrases = userService.getAllPhrases();
		Map<String, String> phrasesAsignedToUsers = new HashMap<>();
		
		for(User user: allUsers) {
			History newUserHistory = new History();
			Integer userPhraseType = user.getConfig().getPhraseType();
			List<History> userHistory = user.getHistory();
			List<Phrase> filteredPhrases = filterPhrasesByType(allPhrases, userPhraseType);
			List<Phrase> availablePhrasesForUser = filterPhraseByAvailability(filteredPhrases, userHistory);
			
			if(availablePhrasesForUser.size() > 0) {
				Random randomElement = new Random();
				Phrase randomPhraseSelected = availablePhrasesForUser.get(randomElement.nextInt(availablePhrasesForUser.size()));
				
				try {
					newUserHistory.setPhraseId(randomPhraseSelected.getId());
					newUserHistory.setUser(user);
					historyService.save(newUserHistory);
				} catch (DataAccessException e) {
					response.put("msg", "Error al intentar guardar el registro");
					response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
				//LOGGER.info("USER: " + user.getName());
				//LOGGER.info("CHOSEN PHRASE: " + randomPhraseSelected.getBody());	
			}
		}
		
		response.put("phrasesAsigned", phrasesAsignedToUsers);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Filter the phrase list based on the user phrase type
	 */
	private List<Phrase> filterPhrasesByType(List<Phrase> allPhrases, Integer phraseType){
		return allPhrases
				.stream()
				.filter(phrase -> {
					Long phraseTypeCastedToLong = Long.valueOf(phraseType.longValue());
					if(phrase.getType().getId().equals(phraseTypeCastedToLong)) {
						return true;
					}
					return false;
				})
				.collect(Collectors.toList());
	}
	
	/*
	 * Filter the phrase list based on the user history list, if the user has the phrase on this history then its removed from the "allPhrases" list
	 */
	private List<Phrase> filterPhraseByAvailability(List<Phrase> allPhrases, List<History> userHistory) {
		for(History history: userHistory) {
			allPhrases.removeIf(phrase -> (history.getPhraseId().equals(phrase.getId())));
		}
		return allPhrases;
	}
}
