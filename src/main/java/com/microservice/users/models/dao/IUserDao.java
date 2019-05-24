package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.User;

public interface IUserDao extends CrudRepository<User, Long>{

}