package com.microservice.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.microservice.users.enums.DatabaseMessagesEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NullRecordException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NullRecordException() {
		super(DatabaseMessagesEnum.NOT_FOUND.getMessage());
	}
}
