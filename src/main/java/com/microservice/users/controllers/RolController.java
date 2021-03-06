package com.microservice.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.utils.Messages;
import lombok.extern.slf4j.Slf4j;
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
import com.microservices.commons.models.entity.users.Rol;
import com.microservice.users.models.services.IRolService;

@Slf4j
@RestController
public class RolController {
	
	@Autowired
	private IRolService rolService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/roles", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Rol> index(){
		return rolService.findAll();
	}
	
	@GetMapping(path="/roles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {

		Rol rol = null;

		try {
			log.info(Messages.findObjectMessage("Rol", id.toString()));
			rol = rolService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		if (rol == null) {
			log.error(Messages.nullObjectMessage("Rol", id.toString()));
			throw new NullRecordException();
		}

		return new ResponseEntity<Rol>(rol, HttpStatus.OK);
	}
	
	@PostMapping(path="/roles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Rol rol, BindingResult result) throws DatabaseAccessException {
		
		Rol newRol = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsCreatingObjectMessage("Rol"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("Rol"));
			newRol = rolService.save(rol);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("Rol", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
		response.put("rol", newRol);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/roles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Rol rol, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Rol rolFromDB = rolService.findById(id);
		Rol rolUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsUpdatingObjectMessage("Rol", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (rolFromDB == null) {
			log.error(Messages.nullObjectMessage("Rol", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("Rol", id.toString()));
			rolFromDB.setName(rol.getName());
			rolFromDB.setDescription(rol.getDescription());
			rolUpdated = rolService.save(rolFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("Rol", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("rol", rolUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/roles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("Rol", id.toString()));
			rolService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("Rol", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
