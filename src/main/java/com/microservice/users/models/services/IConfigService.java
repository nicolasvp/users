package com.microservice.users.models.services;

import java.util.List;
import com.microservices.commons.models.entity.users.Config;

public interface IConfigService {

	public List<Config> findAll();
	
	public Config findById(Long id);
	
	public Config save(Config config);
	
	public void delete(Long id);
}
