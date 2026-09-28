package fr.ryan.api_kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import fr.ryan.api_kanban.repository.CardRepository;
import fr.ryan.api_kanban.repository.KanbanListRepository;
import fr.ryan.api_kanban.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CardDeleteIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KanbanListRepository kanbanListRepository;

	@Autowired
	private CardRepository cardRepository;

	@BeforeEach
	void clean() {
		cardRepository.deleteAll();
		kanbanListRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void deleteCard_asListOwner_returns204() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");
		String listId = createList(token, "Backlog");
		String cardId = createCard(token, listId, "Write docs");

		mockMvc.perform(delete("/api/cards/" + cardId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/cards/" + cardId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.andExpect(status().isNotFound());
	}

	@Test
	void deleteCard_otherOwner_returns403() throws Exception {
		register("alice@example.com", "password1", "Alice");
		register("bob@example.com", "password1", "Bob");
		String aliceToken = loginToken("alice@example.com", "password1");
		String bobToken = loginToken("bob@example.com", "password1");
		String bobListId = createList(bobToken, "Bob list");
		String bobCardId = createCard(bobToken, bobListId, "Secret");

		mockMvc.perform(delete("/api/cards/" + bobCardId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + aliceToken))
			.andExpect(status().isForbidden());
	}

	@Test
	void deleteCard_unknownId_returns404() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(delete("/api/cards/" + UUID.randomUUID())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.andExpect(status().isNotFound());
	}

	@Test
	void deleteCard_unauthenticated_returns401() throws Exception {
		mockMvc.perform(delete("/api/cards/" + UUID.randomUUID()))
			.andExpect(status().isUnauthorized());
	}

	private String createCard(String token, String listId, String title) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/lists/" + listId + "/cards")
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
