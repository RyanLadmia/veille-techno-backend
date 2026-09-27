package fr.ryan.api_kanban.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.CreateListRequest;
import fr.ryan.api_kanban.dto.ListResponse;
import fr.ryan.api_kanban.dto.UpdateListRequest;
import fr.ryan.api_kanban.entity.KanbanList;
import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.exception.ForbiddenException;
import fr.ryan.api_kanban.exception.NotFoundException;
import fr.ryan.api_kanban.exception.UnauthorizedException;
import fr.ryan.api_kanban.repository.KanbanListRepository;
import fr.ryan.api_kanban.repository.UserRepository;

@Service
public class ListService {

	private final KanbanListRepository kanbanListRepository;
	private final UserRepository userRepository;

	public ListService(KanbanListRepository kanbanListRepository, UserRepository userRepository) {
		this.kanbanListRepository = kanbanListRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<ListResponse> getMyLists() {
		UUID ownerId = currentUserId();
		return kanbanListRepository.findByOwner_Id(ownerId).stream()
			.map(ListResponse::from)
			.toList();
	}

	@Transactional
	public ListResponse createList(CreateListRequest request) {
		UUID ownerId = currentUserId();
		User owner = userRepository.findById(ownerId)
			.orElseThrow(UnauthorizedException::new);

		KanbanList list = new KanbanList();
		list.setTitle(request.getTitle());
		list.setPosition(request.getPosition() != null ? request.getPosition() : 0);
		list.setOwner(owner);

		return ListResponse.from(kanbanListRepository.save(list));
	}

	@Transactional
	public ListResponse updateList(UUID id, UpdateListRequest request) {
		UUID ownerId = currentUserId();
		KanbanList list = kanbanListRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("List not found"));

		if (!list.getOwnerId().equals(ownerId)) {
			throw new ForbiddenException("You are not the owner of this list");
		}

		if (request.getTitle() != null) {
			list.setTitle(request.getTitle());
		}
		if (request.getPosition() != null) {
			list.setPosition(request.getPosition());
		}

		return ListResponse.from(kanbanListRepository.save(list));
	}

	@Transactional
	public void deleteList(UUID id) {
		UUID ownerId = currentUserId();
		KanbanList list = kanbanListRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("List not found"));

		if (!list.getOwnerId().equals(ownerId)) {
			throw new ForbiddenException("You are not the owner of this list");
		}

		// Cards are removed with the list (CascadeType.ALL + orphanRemoval on KanbanList.cards).
		kanbanListRepository.delete(list);
	}

	private UUID currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
			throw new UnauthorizedException();
		}
		return userId;
	}
}
