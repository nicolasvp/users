package com.microservice.users.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.microservices.commons.models.entity.users.User;

public interface IUserDao extends CrudRepository<User, Long>{

	@Query("select u from User u where u.username=?1")
	public User findByUsername(@Param("username") String username);
}
