package fr.ryan.api_kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
class ListUpdateIntegrationTest {

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
	void updateList_asOwner_returns200() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");
		String listId = createList(token, "Backlog");

		mockMvc.perform(patch("/api/lists/" + listId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Done",
					  "position": 2
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("Done"))
			.andExpect(jsonPath("$.position").value(2));
	}

	@Test
	void updateList_otherOwner_returns403() throws Exception {
		register("alice@example.com", "password1", "Alice");
		register("bob@example.com", "password1", "Bob");
		String aliceToken = loginToken("alice@example.com", "password1");
		String bobToken = loginToken("bob@example.com", "password1");
		String bobListId = createList(bobToken, "Bob list");

		mockMvc.perform(patch("/api/lists/" + bobListId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + aliceToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Hacked"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	void updateList_unknownId_returns404() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/lists/" + UUID.randomUUID())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "Nope"
					}
					"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void updateList_blankTitle_returns400() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");
		String listId = createList(token, "Backlog");

		mockMvc.perform(patch("/api/lists/" + listId)
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

	private String createList(String token, String title) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "title": "%s"
					}
					""".formatted(title)))
			.andExpect(status().isCreated())
			.andReturn();
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

	private void register(String email, String password, String name) throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "%s",
					  "name": "%s"
					}
					""".formatted(email, password, name)))
			.andExpect(status().isCreated());
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
