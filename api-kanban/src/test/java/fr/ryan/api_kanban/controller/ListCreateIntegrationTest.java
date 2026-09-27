package fr.ryan.api_kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import fr.ryan.api_kanban.repository.KanbanListRepository;
import fr.ryan.api_kanban.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ListCreateIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KanbanListRepository kanbanListRepository;

	@BeforeEach
	void clean() {
		kanbanListRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void createList_nominal_returns201WithOwnerId() throws Exception {
		String userId = register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(post("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Backlog"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.title").value("Backlog"))
			.andExpect(jsonPath("$.ownerId").value(userId))
			.andExpect(jsonPath("$.position").value(0))
			.andExpect(jsonPath("$.createdAt").isNotEmpty());
	}

	@Test
	void createList_blankTitle_returns400() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(post("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.title").exists());
	}

	@Test
	void createList_missingTitle_returns400() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(post("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.title").exists());
	}

	@Test
	void createList_withoutToken_returns401() throws Exception {
		mockMvc.perform(post("/api/lists")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Backlog"
					}
					"""))
			.andExpect(status().isUnauthorized());
	}

	private String register(String email, String password, String name) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "%s",
					  "name": "%s"
					}
					""".formatted(email, password, name)))
			.andExpect(status().isCreated())
			.andReturn();
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

	private String loginToken(String email, String password) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "%s"
					}
					""".formatted(email, password)))
			.andExpect(status().isOk())
			.andReturn();
		return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
	}
}
