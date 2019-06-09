package com.microservice.users.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.users.models.dao.ILikeDao;
import com.microservice.users.models.entity.Likes;

@Service
public class LikesServiceImpl implements ILikesService{

	@Autowired
	ILikeDao likeDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Likes> findAll() {
		return (List<Likes>) likeDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Likes findById(Long id) {
		return likeDao.findById(id).orElse(null);
	}

	@Override
	public Likes save(Likes like) {
		return likeDao.save(like);
	}

	@Override
	public void delete(Long id) {
		likeDao.deleteById(id);
	}

}
