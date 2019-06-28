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

import com.microservice.users.models.entity.Config;
import com.microservice.users.models.services.IConfigService;

@RestController
@RequestMapping("/api")
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
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Config config = null;
		Map<String, Object> response = new HashMap<>();

		try {
			config = configService.findById(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// return error if the record non exist
		if (config == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}
	
	@PostMapping(path="/config", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Config config, BindingResult result) {
		
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
			LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("config", newConfig);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/config/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Config config, BindingResult result, @PathVariable("id") Long id) {
		
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
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			configFromDB.setUser(config.getUser());
			configFromDB.setLanguage(config.getLanguage());
			configUpdated = configService.save(configFromDB);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("config", configUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/config/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			configService.delete(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar eliminar el registro de la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
