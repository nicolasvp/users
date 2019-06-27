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

import com.microservice.users.models.entity.Likes;
import com.microservice.users.models.services.ILikesService;

@RestController
@RequestMapping("/api")
public class LikesController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(LikesController.class);
	
	@Autowired
	private ILikesService likesService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/likes", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Likes> index(){
		return likesService.findAll();
	}
	
	@GetMapping(path="/likes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Likes like = null;
		Map<String, Object> response = new HashMap<>();

		try {
			like = likesService.findById(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// return error if the record non exist
		if (like == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Likes>(like, HttpStatus.OK);
	}
	
	@PostMapping(path="/likes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Likes like, BindingResult result) {
		
		Likes newLikes = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newLikes = likesService.save(like);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("like", newLikes);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/likes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Likes like, BindingResult result, @PathVariable("id") Long id) {
		
		Likes likeFromDB = likesService.findById(id);
		Likes likeUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (likeFromDB == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			likeFromDB.setUser(like.getUser());
			likeFromDB.setPhraseId(like.getPhraseId());
			likeUpdated = likesService.save(likeFromDB);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("like", likeUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/likes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			likesService.delete(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar eliminar el registro de la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
