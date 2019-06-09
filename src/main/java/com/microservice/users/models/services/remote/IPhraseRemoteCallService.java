package com.microservice.users.models.services.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.microservice.users.models.services.remote.entity.Phrase;

@FeignClient(name="PHRASES-SERVICE") // Service name registered on Eureka Server
public interface IPhraseRemoteCallService {
	
	@RequestMapping(method=RequestMethod.GET, value="/api/service-route")
	public String getServiceRoute();
	
	@RequestMapping(method=RequestMethod.GET, value="/api/phrases")
	public List<Phrase> getAllPhrases();
}
