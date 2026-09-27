package fr.ryan.api_kanban.exception;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("Email already used");
	}
}
