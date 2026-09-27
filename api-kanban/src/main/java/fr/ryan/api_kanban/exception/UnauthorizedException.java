package fr.ryan.api_kanban.exception;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super("Unauthorized");
	}
}
