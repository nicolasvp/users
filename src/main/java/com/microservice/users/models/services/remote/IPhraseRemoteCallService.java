package com.microservice.users.models.services.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.microservice.users.models.services.remote.entity.Phrase;

@FeignClient(name="PHRASES-SERVICE") // Service name registered on Eureka Server
public interface IPhraseRemoteCallService {
	
	@GetMapping("/api/service-route")
	public String getServiceRoute();
	
	@GetMapping("/api/phrases")
	public List<Phrase> getAllPhrases();
}
