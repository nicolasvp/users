package com.microservice.users.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IUserService;

@RestController
@RequestMapping("/api")
public class UserController {

	protected Logger LOGGER = Logger.getLogger(UserController.class.getName());
	
	@Autowired
	private IUserService userService;
	
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
}
