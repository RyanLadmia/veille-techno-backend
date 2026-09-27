package fr.ryan.api_kanban.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.UserResponse;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.exception.UnauthorizedException;
import fr.ryan.api_kanban.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public UserResponse getCurrentUser() {
		UUID userId = currentUserId();
		User user = userRepository.findById(userId)
			.orElseThrow(UnauthorizedException::new);
		return UserResponse.from(user);
	}

	private UUID currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
			throw new UnauthorizedException();
		}
		return userId;
	}
}
