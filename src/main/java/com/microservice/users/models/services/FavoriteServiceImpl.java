package com.microservice.users.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.users.models.dao.IFavoriteDao;
import com.microservice.users.models.entity.Favorite;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

	@Autowired
	IFavoriteDao favoriteDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Favorite> findAll() {
		return (List<Favorite>) favoriteDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Favorite findById(Long id) {
		return favoriteDao.findById(id).orElse(null);
	}

	@Override
	public Favorite save(Favorite favorite) {
		return favoriteDao.save(favorite);
	}

	@Override
	public void delete(Long id) {
		favoriteDao.deleteById(id);
	}

}
