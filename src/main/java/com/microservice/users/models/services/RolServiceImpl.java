package com.microservice.users.models.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservice.users.models.dao.IRolDao;
import com.microservices.commons.models.entity.users.Rol;

@Service
public class RolServiceImpl implements IRolService{

	@Autowired
	private IRolDao rolDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Rol> findAll() {
		return (List<Rol>) rolDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Rol findById(Long id) {
		return rolDao.findById(id).orElse(null);
	}

	@Override
	public Rol save(Rol rol) {
		return rolDao.save(rol);
	}

	@Override
	public void delete(Long id) {
		rolDao.deleteById(id);
	}

}
