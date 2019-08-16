package com.microservice.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
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
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.users.Config;
import com.microservice.users.models.services.IConfigService;

@RestController
public class ConfigController {

	protected Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);
	
	@Autowired
	private IConfigService configService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/config", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Config> index(){
		return configService.findAll();
	}
	
	@GetMapping(path="/config/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		Config config = null;

		try {
			config = configService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (config == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}
	
	@PostMapping(path="/config", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Config config, BindingResult result) throws DatabaseAccessException {
		
		Config newConfig = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newConfig = configService.save(config);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
		response.put("config", newConfig);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/config/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Config config, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Config configFromDB = configService.findById(id);
		Config configUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (configFromDB == null) {
			throw new NullRecordException();
		}

		try {
			configFromDB.setUser(config.getUser());
			configFromDB.setLanguage(config.getLanguage());
			configUpdated = configService.save(configFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("config", configUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/config/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			configService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
