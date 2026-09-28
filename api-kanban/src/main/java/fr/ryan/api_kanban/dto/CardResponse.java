package fr.ryan.api_kanban.dto;

import java.time.Instant;
import java.util.UUID;

import fr.ryan.api_kanban.entity.Card;

public class CardResponse {

	private UUID id;
	private String title;
	private String description;
	private int position;
	private UUID listId;
	private Instant createdAt;
	private Instant updatedAt;

	public static CardResponse from(Card card) {
		CardResponse response = new CardResponse();
		response.id = card.getId();
		response.title = card.getTitle();
		response.description = card.getDescription();
		response.position = card.getPosition();
		response.listId = card.getListId();
		response.createdAt = card.getCreatedAt();
		response.updatedAt = card.getUpdatedAt();
		return response;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getPosition() {
		return position;
	}

	public UUID getListId() {
		return listId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
