package com.microservice.users.models.services;

import java.util.List;

import com.microservice.users.models.entity.Language;
import com.microservice.users.models.entity.Likes;

public interface ILikesService {
	
	public List<Likes> findAll();
	
	public Likes findById(Long id);
	
	public Likes save(Likes likes);
	
	public void delete(Long id);
}
