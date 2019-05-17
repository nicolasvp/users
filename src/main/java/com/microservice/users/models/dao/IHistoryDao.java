package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.History;

public interface IHistoryDao extends CrudRepository<History, Long>{

}
