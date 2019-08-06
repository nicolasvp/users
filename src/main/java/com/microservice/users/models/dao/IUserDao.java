package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.users.User;

public interface IUserDao extends CrudRepository<User, Long>{

}
