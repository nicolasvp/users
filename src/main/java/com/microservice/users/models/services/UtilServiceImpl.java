package com.microservice.users.models.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilServiceImpl implements IUtilService {

    @Override
    public List<String> listErrors(BindingResult result) {
        List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());
        return errors;
    }
}
