package com.microservice.users.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.users.models.entity.Language;

public interface ILanguageDao extends CrudRepository<Language, Long>{

}
