package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.users.Rol;

public interface IRolDao extends CrudRepository<Rol, Long>{

}
