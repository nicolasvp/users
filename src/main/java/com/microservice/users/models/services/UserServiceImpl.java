package com.microservice.users.models.services;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservice.users.models.dao.IUserDao;
import com.microservices.commons.models.entity.users.User;
import com.microservice.users.models.services.remote.IPhraseRemoteCallService;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Slf4j
@Service
public class UserServiceImpl implements IUserService{
	
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
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
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
		log.info("Invoking phrases service from users service");
		return remoteCaller.getServiceRoute();
	}

	@Override
	@HystrixCommand(fallbackMethod = "getAllPhrasesFail")
	public List<Phrase> getAllPhrases() {
		log.info("Getting all phrases from phrase service");
		return remoteCaller.getAllPhrases();
	}
	
	@Override
	public List<Phrase> getAllPhrasesFail() {
		return new ArrayList<>();
	}
	
	@Override
	public String unavailableMessage() {
		return "Phrases service is not available";
	}
}
