package com.microservice.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.microservice.users.enums.CrudMessagesEnum;
import com.microservice.users.enums.DatabaseMessagesEnum;
import com.microservice.users.exceptions.DatabaseAccessException;
import com.microservice.users.exceptions.NullRecordException;
import com.microservice.users.models.entity.Favorite;
import com.microservice.users.models.services.IFavoriteService;

@RestController
@RequestMapping("/api")
public class FavoriteController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);
	
	@Autowired
	private IFavoriteService favoriteService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/favorities", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Favorite> index(){
		return favoriteService.findAll();
	}
	
	@GetMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		Favorite favorite = null;

		try {
			favorite = favoriteService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (favorite == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<Favorite>(favorite, HttpStatus.OK);
	}
	
	@PostMapping(path="/favorities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Favorite favorite, BindingResult result) throws DatabaseAccessException {
		
		Favorite newFavorite = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newFavorite = favoriteService.save(favorite);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED_MESSAGE.getMessage());
		response.put("favorite", newFavorite);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Favorite favorite, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Favorite favoriteFromDB = favoriteService.findById(id);
		Favorite favoriteUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (favoriteFromDB == null) {
			throw new NullRecordException();
		}

		try {
			favoriteFromDB.setUser(favorite.getUser());
			favoriteFromDB.setPhraseId(favorite.getPhraseId());
			favoriteUpdated = favoriteService.save(favoriteFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED_MESSAGE.getMessage());
		response.put("favorite", favoriteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			favoriteService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED_MESSAGE.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
