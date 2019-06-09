package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.Likes;

public interface ILikeDao extends CrudRepository<Likes, Long>{

}
