package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.Rol;

public interface IRolService {

	public List<Rol> findAll();
	
	public Rol findById(Long id);
	
	public Rol save(Rol rol);
	
	public void delete(Long id);
}
