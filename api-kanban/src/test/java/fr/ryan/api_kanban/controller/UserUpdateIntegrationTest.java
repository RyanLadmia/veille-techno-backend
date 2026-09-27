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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import fr.ryan.api_kanban.entity.Role;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UserUpdateIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void cleanUsers() {
		userRepository.deleteAll();
	}

	@Test
	void update_ownProfile_returns200WithoutPassword() throws Exception {
		UUID userId = register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + userId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "Alice Updated",
					  "email": "alice.updated@example.com"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Alice Updated"))
			.andExpect(jsonPath("$.email").value("alice.updated@example.com"))
			.andExpect(jsonPath("$.role").value("user"))
			.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void update_otherProfileAsNonAdmin_returns403() throws Exception {
		UUID bobId = register("bob@example.com", "password1", "Bob");
		register("alice@example.com", "password1", "Alice");
		String aliceToken = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + bobId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + aliceToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "Hacked"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	void update_ownRoleAsNonAdmin_returns403() throws Exception {
		UUID userId = register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + userId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "role": "admin"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	void update_unknownId_returns404() throws Exception {
		register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + UUID.randomUUID())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "Nope"
					}
					"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void update_invalidRole_returns400() throws Exception {
		UUID userId = register("alice@example.com", "password1", "Alice");
		String token = loginToken("alice@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + userId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "role": "superadmin"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.role").exists());
	}

	@Test
	void update_otherUserRoleAsAdmin_returns200() throws Exception {
		UUID bobId = register("bob@example.com", "password1", "Bob");
		createAdmin("admin@example.com", "password1", "Admin");
		String adminToken = loginToken("admin@example.com", "password1");

		mockMvc.perform(patch("/api/users/" + bobId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "role": "admin"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.role").value("admin"))
			.andExpect(jsonPath("$.password").doesNotExist());
	}

	private UUID register(String email, String password, String name) throws Exception {
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
		return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
	}

	private void createAdmin(String email, String password, String name) {
		User admin = new User();
		admin.setEmail(email);
		admin.setPassword(passwordEncoder.encode(password));
		admin.setName(name);
		admin.setRole(Role.ADMIN);
		userRepository.save(admin);
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
