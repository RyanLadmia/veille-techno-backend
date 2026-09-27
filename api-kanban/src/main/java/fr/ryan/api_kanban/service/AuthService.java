package fr.ryan.api_kanban.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.AuthTokenResponse;
import fr.ryan.api_kanban.dto.LoginRequest;
import fr.ryan.api_kanban.dto.RegisterRequest;
import fr.ryan.api_kanban.dto.UserResponse;
import fr.ryan.api_kanban.entity.Role;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.exception.EmailAlreadyUsedException;
import fr.ryan.api_kanban.exception.InvalidCredentialsException;
import fr.ryan.api_kanban.repository.UserRepository;
import fr.ryan.api_kanban.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final String dummyPasswordHash;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.dummyPasswordHash = passwordEncoder.encode("timing-safe-dummy");
	}

	@Transactional
	public UserResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyUsedException();
		}

		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setName(request.getName());
		user.setRole(Role.USER);

		User saved = userRepository.save(user);
		return UserResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public AuthTokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);
		String hashToCheck = user != null ? user.getPassword() : dummyPasswordHash;
		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), hashToCheck);

		if (user == null || !passwordMatches) {
			throw new InvalidCredentialsException();
		}

		return new AuthTokenResponse(jwtService.createToken(user));
	}
}
