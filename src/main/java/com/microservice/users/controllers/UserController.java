package com.microservice.users.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IUserService;
import com.microservice.users.models.services.remote.IPhraseRemoteCallService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/api")
public class UserController {

	protected Logger LOGGER = Logger.getLogger(UserController.class.getName());
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IPhraseRemoteCallService loadBalancer;
	
	@GetMapping("/users")
	public List<User> index(){
		return userService.findAll();
	}
	
	@GetMapping("/service-route")
	public String serviceRoute() {
		return "Hi from users service";
	}
	
	@HystrixCommand(fallbackMethod = "unavailableMessage")
	@GetMapping("/users/phrases")
	public String users(){
		LOGGER.info("Invoking phrases service from users service");
		String response = loadBalancer.getServiceRoute();
		return response;
	}
		
	public String unavailableMessage() {
		return "Phrases service is not available";
	}
}
