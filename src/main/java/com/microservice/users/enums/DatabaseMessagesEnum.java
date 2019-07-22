package com.microservice.users.enums;

public enum DatabaseMessagesEnum {

	NOT_FOUND("The record does not exist on database", 1),
	ACCESS_DATABASE("Error trying to access to database", 2),
	STORE_RECORD("Error trying to store record", 3),
	UPDATE_RECORD("Error trying to update record", 4),
	DELETE_RECORD("Error trying to delete record", 5);

	private String message;
	
	DatabaseMessagesEnum(String message, int ordinal) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
