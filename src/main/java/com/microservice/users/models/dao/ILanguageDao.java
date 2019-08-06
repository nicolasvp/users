package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.users.Language;

public interface ILanguageDao extends CrudRepository<Language, Long>{

}
