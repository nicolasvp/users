package com.microservice.users.models.services;

import java.util.List;
import com.microservices.commons.models.entity.users.Rol;

public interface IRolService {

	public List<Rol> findAll();
	
	public Rol findById(Long id);
	
	public Rol save(Rol rol);
	
	public void delete(Long id);
}
