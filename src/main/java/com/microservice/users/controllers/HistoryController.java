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

import com.microservice.users.config.MessagesTranslate;
import com.microservice.users.enums.DatabaseMessagesEnum;
import com.microservice.users.exceptions.DatabaseAccessException;
import com.microservice.users.exceptions.NullRecordException;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.services.IHistoryService;

@RestController
@RequestMapping("/api")
public class HistoryController {

	protected Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);
	
	@Autowired
	private IHistoryService historyService;

	@Autowired
	private IUtilService utilService;

	@Autowired
	private MessagesTranslate messages;
	
	@GetMapping(path="/histories", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<History> index(){
		return historyService.findAll();
	}
	
	@GetMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		History history = null;

		try {
			history = historyService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (history == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<History>(history, HttpStatus.OK);
	}
	
	@PostMapping(path="/histories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody History history, BindingResult result) throws DatabaseAccessException {
		
		History newHistory = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newHistory = historyService.save(history);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getCreated());
		response.put("history", newHistory);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody History history, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		History historyFromDB = historyService.findById(id);
		History historyUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (historyFromDB == null) {
			throw new NullRecordException();
		}

		try {
			historyFromDB.setUser(history.getUser());
			historyFromDB.setPhraseId(history.getPhraseId());
			historyUpdated = historyService.save(historyFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getUpdated());
		response.put("history", historyUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			historyService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getDeleted());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
