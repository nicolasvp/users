package com.microservice.users.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservice.users.models.dao.IConfigDao;
import com.microservices.commons.models.entity.users.Config;

@Service
public class ConfigServiceImpl implements IConfigService {

	@Autowired
	IConfigDao configDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Config> findAll() {
		return (List<Config>) configDao.findAll(); 
	}

	@Override
	@Transactional(readOnly = true)
	public Config findById(Long id) {
		return configDao.findById(id).orElse(null);
	}

	@Override
	public Config save(Config config) {
		return configDao.save(config);
	}

	@Override
	public void delete(Long id) {
		configDao.deleteById(id);
	}

}
