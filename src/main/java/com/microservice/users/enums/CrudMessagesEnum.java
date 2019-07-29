package com.microservice.users.enums;

public enum CrudMessagesEnum {

	CREATED_MESSAGE("Record succesfully created", 1),
	UPDATED_MESSAGE("Record succesfully updated", 2),
	DELETED_MESSAGE("Record succesfully deleted", 3);

	private String message;
	
	CrudMessagesEnum(String message, int ordinal) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
