package fr.ryan.api_kanban.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCardRequest {

	@NotBlank
	private String title;

	private String description;

	private Integer position;

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
}
