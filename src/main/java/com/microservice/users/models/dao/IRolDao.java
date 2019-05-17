package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.Rol;

public interface IRolDao extends CrudRepository<Rol, Long>{

}
