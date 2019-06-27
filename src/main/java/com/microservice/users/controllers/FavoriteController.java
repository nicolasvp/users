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
import com.microservice.users.models.services.IFavoriteService;

@RestController
@RequestMapping("/api")
public class FavoriteController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);
	
	@Autowired
	private IFavoriteService favoriteService;

	@Autowired
	private IUtilService utilService;

	@GetMapping("/favorities")
	public List<Favorite> index(){
		return favoriteService.findAll();
	}
	
	@GetMapping("/favorities/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Favorite favorite = null;
		Map<String, Object> response = new HashMap<>();

		try {
			favorite = favoriteService.findById(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		// return error if the record non exist
		if (favorite == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Favorite>(favorite, HttpStatus.OK);
	}
	
	@PostMapping("/favorities")
	public ResponseEntity<?> create(@Valid @RequestBody Favorite favorite, BindingResult result) {
		
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
			LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("favorite", newFavorite);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/favorities/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Favorite favorite, BindingResult result, @PathVariable("id") Long id) {
		
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
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			favoriteFromDB.setUser(favorite.getUser());
			favoriteFromDB.setPhraseId(favorite.getPhraseId());
			favoriteUpdated = favoriteService.save(favoriteFromDB);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("favorite", favoriteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/favorities/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			favoriteService.delete(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar eliminar el registro de la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
