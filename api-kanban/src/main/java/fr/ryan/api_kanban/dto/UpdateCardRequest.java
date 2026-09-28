package fr.ryan.api_kanban.dto;

import java.util.UUID;

import jakarta.validation.constraints.Size;

public class UpdateCardRequest {

	@Size(min = 1)
	private String title;

	private String description;

	private Integer position;

	private UUID listId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public UUID getListId() {
		return listId;
	}

	public void setListId(UUID listId) {
		this.listId = listId;
	}
}
