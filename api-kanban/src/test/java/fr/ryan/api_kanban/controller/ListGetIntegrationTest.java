package fr.ryan.api_kanban.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import fr.ryan.api_kanban.entity.KanbanList;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.repository.KanbanListRepository;
import fr.ryan.api_kanban.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ListGetIntegrationTest {

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
	void getLists_returnsOnlyCurrentUserLists() throws Exception {
		register("alice@example.com", "password1", "Alice");
		register("bob@example.com", "password1", "Bob");

		User alice = userRepository.findByEmail("alice@example.com").orElseThrow();
		User bob = userRepository.findByEmail("bob@example.com").orElseThrow();

		saveList(alice, "Alice Todo", 0);
		saveList(alice, "Alice Doing", 1);
		saveList(bob, "Bob Secret", 0);

		String aliceToken = loginToken("alice@example.com", "password1");

		mockMvc.perform(get("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + aliceToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].title").exists())
			.andExpect(jsonPath("$[?(@.title == 'Bob Secret')]").isEmpty())
			.andExpect(jsonPath("$[?(@.title == 'Alice Todo')]").isNotEmpty())
			.andExpect(jsonPath("$[0].ownerId").value(alice.getId().toString()))
			.andExpect(jsonPath("$[0].password").doesNotExist());
	}

	@Test
	void getLists_whenEmpty_returnsEmptyArray() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(get("/api/lists")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getLists_withoutToken_returns401() throws Exception {
		mockMvc.perform(get("/api/lists"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("Unauthorized"));
	}

	private void saveList(User owner, String title, int position) {
		KanbanList list = new KanbanList();
		list.setOwner(owner);
		list.setTitle(title);
		list.setPosition(position);
		kanbanListRepository.save(list);
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
