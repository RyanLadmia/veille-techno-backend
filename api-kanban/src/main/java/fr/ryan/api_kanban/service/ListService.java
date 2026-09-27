package fr.ryan.api_kanban.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.ListResponse;
import fr.ryan.api_kanban.exception.UnauthorizedException;
import fr.ryan.api_kanban.repository.KanbanListRepository;

@Service
public class ListService {

	private final KanbanListRepository kanbanListRepository;

	public ListService(KanbanListRepository kanbanListRepository) {
		this.kanbanListRepository = kanbanListRepository;
	}

	@Transactional(readOnly = true)
	public List<ListResponse> getMyLists() {
		UUID ownerId = currentUserId();
		return kanbanListRepository.findByOwner_Id(ownerId).stream()
			.map(ListResponse::from)
			.toList();
	}

	private UUID currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
			throw new UnauthorizedException();
		}
		return userId;
	}
}
