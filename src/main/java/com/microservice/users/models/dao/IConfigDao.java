package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.Config;

public interface IConfigDao extends CrudRepository<Config, Long>{

}
