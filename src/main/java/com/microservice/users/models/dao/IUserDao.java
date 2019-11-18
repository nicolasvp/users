package com.microservice.users.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.microservices.commons.models.entity.users.User;

//@RepositoryRestResource(path="users")
public interface IUserDao extends PagingAndSortingRepository<User, Long>{

	//@Query("select u from User u where u.username=?1")
	//@RestResource(path="username")
	public User findByUsername(@Param("username") String username);
}
