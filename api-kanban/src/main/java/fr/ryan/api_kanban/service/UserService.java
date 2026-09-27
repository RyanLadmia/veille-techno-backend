package fr.ryan.api_kanban.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.UpdateUserRequest;
import fr.ryan.api_kanban.dto.UserResponse;
import fr.ryan.api_kanban.entity.Role;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.exception.ForbiddenException;
import fr.ryan.api_kanban.exception.NotFoundException;
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

	@Transactional
	public UserResponse updateUser(UUID id, UpdateUserRequest request) {
		UUID currentUserId = currentUserId();
		User currentUser = userRepository.findById(currentUserId)
			.orElseThrow(UnauthorizedException::new);
		User target = userRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("User not found"));

		boolean isAdmin = currentUser.getRole() == Role.ADMIN;
		boolean isSelf = currentUser.getId().equals(target.getId());

		if (!isSelf && !isAdmin) {
			throw new ForbiddenException("You cannot modify another user's profile");
		}

		if (request.getRole() != null) {
			if (!isAdmin) {
				throw new ForbiddenException("Only an admin can change a role");
			}
			target.setRole(Role.valueOf(request.getRole().toUpperCase()));
		}

		if (request.getName() != null) {
			target.setName(request.getName());
		}
		if (request.getEmail() != null) {
			target.setEmail(request.getEmail());
		}

		return UserResponse.from(userRepository.save(target));
	}

	private UUID currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
			throw new UnauthorizedException();
		}
		return userId;
	}
}
