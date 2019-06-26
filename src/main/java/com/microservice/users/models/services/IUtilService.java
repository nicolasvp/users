package com.microservice.users.models.services;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public interface IUtilService {

    public List<String> listErrors(BindingResult result);
}
