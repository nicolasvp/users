package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.Config;

public interface IConfigService {

	public List<Config> findAll();
	
	public Config findById(Long id);
	
	public Config save(Config config);
	
	public void delete(Long id);
}