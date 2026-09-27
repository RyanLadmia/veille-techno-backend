package fr.ryan.api_kanban.dto;

import java.time.Instant;
import java.util.UUID;

import fr.ryan.api_kanban.entity.Role;
import fr.ryan.api_kanban.entity.User;

public class UserResponse {

	private UUID id;
	private String email;
	private String name;
	private String role;
	private Instant createdAt;

	public static UserResponse from(User user) {
		UserResponse response = new UserResponse();
		response.id = user.getId();
		response.email = user.getEmail();
		response.name = user.getName();
		response.role = toApiRole(user.getRole());
		response.createdAt = user.getCreatedAt();
		return response;
	}

	private static String toApiRole(Role role) {
		return role == null ? null : role.name().toLowerCase();
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
