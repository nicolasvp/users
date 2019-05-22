package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.History;

public interface IHistoryService {

	public List<History> findAll();
	
	public History findById(Long id);
	
	public History save(History history);
	
	public void delete(Long id);
}
