package fr.ryan.api_kanban.dto;

import jakarta.validation.constraints.Size;

public class UpdateListRequest {

	@Size(min = 1)
	private String title;

	private Integer position;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
