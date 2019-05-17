package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.Favorite;

public interface IFavoriteDao extends CrudRepository<Favorite, Long>{

}
