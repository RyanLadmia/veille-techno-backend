package fr.ryan.api_kanban.dto;

import java.time.Instant;
import java.util.UUID;

import fr.ryan.api_kanban.entity.KanbanList;

public class ListResponse {

	private UUID id;
	private String title;
	private int position;
	private UUID ownerId;
	private Instant createdAt;

	public static ListResponse from(KanbanList list) {
		ListResponse response = new ListResponse();
		response.id = list.getId();
		response.title = list.getTitle();
		response.position = list.getPosition();
		response.ownerId = list.getOwnerId();
		response.createdAt = list.getCreatedAt();
		return response;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getPosition() {
		return position;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
