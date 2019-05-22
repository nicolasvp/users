package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.Favorite;

public interface IFavoriteService {

	public List<Favorite> findAll();
	
	public Favorite findById(Long id);
	
	public Favorite save(Favorite favorite);
	
	public void delete(Long id);
}
