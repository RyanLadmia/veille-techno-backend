package fr.ryan.api_kanban.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import fr.ryan.api_kanban.repository.UserRepository;
import fr.ryan.api_kanban.security.JwtService;
import io.jsonwebtoken.Claims;

@SpringBootTest
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {

	private static final String GENERIC_401 = "Invalid email or password";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@BeforeEach
	void cleanUsers() {
		userRepository.deleteAll();
	}

	@Test
	void login_nominal_returnsAccessTokenWithSubAndExp() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "alice@example.com",
					  "password": "password1",
					  "name": "Alice"
					}
					"""))
			.andExpect(status().isCreated());

		UUID userId = userRepository.findByEmail("alice@example.com").orElseThrow().getId();

		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "alice@example.com",
					  "password": "password1"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.password").doesNotExist())
			.andReturn();

		String token = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
		assertThat(token).doesNotContain("password1");

		Claims claims = jwtService.parseClaims(token);
		assertThat(claims.getSubject()).isEqualTo(userId.toString());
		assertThat(claims.getExpiration()).isAfter(Instant.now());
		assertThat(claims.getExpiration().toInstant())
			.isBefore(Instant.now().plusSeconds(3600 + 30));
		assertThat(claims.get("password")).isNull();
	}

	@Test
	void login_unknownEmail_returns401GenericMessage() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "nobody@example.com",
					  "password": "password1"
					}
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value(GENERIC_401))
			.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void login_wrongPassword_returns401SameGenericMessage() throws Exception {
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

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "bob@example.com",
					  "password": "wrong-password"
					}
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value(GENERIC_401));
	}
}
