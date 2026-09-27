package fr.ryan.api_kanban.dto;

public class AuthTokenResponse {

	private String accessToken;

	public AuthTokenResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
