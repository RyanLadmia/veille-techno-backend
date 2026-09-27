package fr.ryan.api_kanban.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRegisterIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void cleanUsers() {
		userRepository.deleteAll();
	}

	@Test
	void register_nominal_returns201WithoutPassword() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "alice@example.com",
					  "password": "password1",
					  "name": "Alice"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.email").value("alice@example.com"))
			.andExpect(jsonPath("$.name").value("Alice"))
			.andExpect(jsonPath("$.role").value("user"))
			.andExpect(jsonPath("$.createdAt").isNotEmpty())
			.andExpect(jsonPath("$.password").doesNotExist());

		User stored = userRepository.findByEmail("alice@example.com").orElseThrow();
		assertThat(stored.getPassword()).startsWith("$2");
		assertThat(stored.getPassword()).isNotEqualTo("password1");
	}

	@Test
	void register_duplicateEmail_returns409() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "bob@example.com",
					  "password": "password1",
					  "name": "Bob"
					}
					"""))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "bob@example.com",
					  "password": "password1",
					  "name": "Bobby"
					}
					"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Email already used"))
			.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void register_invalidEmail_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "not-an-email",
					  "password": "password1",
					  "name": "Alice"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation failed"))
			.andExpect(jsonPath("$.errors.email").exists());
	}

	@Test
	void register_shortPassword_returns400() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "carol@example.com",
					  "password": "short",
					  "name": "Carol"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation failed"))
			.andExpect(jsonPath("$.errors.password").exists());
	}
}
