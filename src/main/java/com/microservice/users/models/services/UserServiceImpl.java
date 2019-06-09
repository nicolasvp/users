package com.microservice.users.models.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.users.models.dao.IUserDao;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.remote.IPhraseRemoteCallService;
import com.microservice.users.models.services.remote.entity.Phrase;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class UserServiceImpl implements IUserService{

	protected Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
	
	@Autowired
	IUserDao userDao;
	
	@Autowired
	private IPhraseRemoteCallService remoteCaller;
	
	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return (List<User>) userDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userDao.findById(id).orElse(null);
	}

	@Override
	public User save(User user) {
		return userDao.save(user);
	}

	@Override
	public void delete(Long id) {
		userDao.deleteById(id);
	}

	@Override
	@HystrixCommand(fallbackMethod = "unavailableMessage")
	public String callPhraseService() {
		LOGGER.info("Invoking phrases service from users service");
		String response = remoteCaller.getServiceRoute();
		return response;
	}

	@Override
	@HystrixCommand(fallbackMethod = "shitHole")
	public List<Phrase> getAllPhrases() {
		LOGGER.info("Getting all phrases from phrase service");
		List<Phrase> response = remoteCaller.getAllPhrases();
		return response;
	}
	
	@Override
	public List<Phrase> shitHole() {
		List<Phrase> response = new ArrayList<>();
		return response;
	}
	
	@Override
	public String unavailableMessage() {
		return "Phrases service is not available";
	}

}
