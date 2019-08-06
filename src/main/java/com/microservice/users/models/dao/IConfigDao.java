package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.users.Config;

public interface IConfigDao extends CrudRepository<Config, Long>{

}
