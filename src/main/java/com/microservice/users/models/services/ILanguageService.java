package com.microservice.users.models.services;

import java.util.List;
import com.microservices.commons.models.entity.users.Language;

public interface ILanguageService {

	public List<Language> findAll();
	
	public Language findById(Long id);
	
	public Language save(Language language);
	
	public void delete(Long id);
}
