package com.microservice.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.utils.Messages;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.users.User;
import com.microservice.users.models.services.IUserService;

@Slf4j
@RestController
public class UserController {
	
	@Autowired
	private IUserService userService;
	
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
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		User user = null;

		try {
			log.info(Messages.findObjectMessage("User", id.toString()));
			user = userService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		if (user == null) {
			log.error(Messages.nullObjectMessage("User", id.toString()));
			throw new NullRecordException();
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@GetMapping(path="/users/search/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> searchByUsername(@PathVariable String username) throws NullRecordException, DatabaseAccessException {
		
		User user = null;

		try {
			log.info(Messages.findUserMessage("User", username));
			user = userService.findByUsername(username);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		if (user == null) {
			log.error(Messages.nullUserMessage("User", username));
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
			log.error(Messages.errorsCreatingObjectMessage("User"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("User"));
			newUser = userService.save(user);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("User", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
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
			log.error(Messages.errorsUpdatingObjectMessage("User", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (userFromDB == null) {
			log.error(Messages.nullObjectMessage("User", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("User", id.toString()));
			userFromDB.setName(user.getName());
			userFromDB.setLastName(user.getLastName());
			userFromDB.setEmail(user.getEmail());
			userFromDB.setPassword(user.getPassword());
			userUpdated = userService.save(userFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("User", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("user", userUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("User", id.toString()));
			userService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("User", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
