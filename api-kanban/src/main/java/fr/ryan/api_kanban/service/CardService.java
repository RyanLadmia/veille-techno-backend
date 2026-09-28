package fr.ryan.api_kanban.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ryan.api_kanban.dto.CardResponse;
import fr.ryan.api_kanban.dto.CreateCardRequest;
import fr.ryan.api_kanban.entity.Card;
import fr.ryan.api_kanban.entity.KanbanList;
import fr.ryan.api_kanban.exception.ForbiddenException;
import fr.ryan.api_kanban.exception.NotFoundException;
import fr.ryan.api_kanban.exception.UnauthorizedException;
import fr.ryan.api_kanban.repository.CardRepository;
import fr.ryan.api_kanban.repository.KanbanListRepository;

@Service
public class CardService {

	private final CardRepository cardRepository;
	private final KanbanListRepository kanbanListRepository;

	public CardService(CardRepository cardRepository, KanbanListRepository kanbanListRepository) {
		this.cardRepository = cardRepository;
		this.kanbanListRepository = kanbanListRepository;
	}

	@Transactional(readOnly = true)
	public List<CardResponse> getCardsForList(UUID listId) {
		requireOwnedList(listId);
		return cardRepository.findByList_IdOrderByPositionAsc(listId).stream()
			.map(CardResponse::from)
			.toList();
	}

	@Transactional
	public CardResponse createCard(UUID listId, CreateCardRequest request) {
		KanbanList list = requireOwnedList(listId);

		Card card = new Card();
		card.setTitle(request.getTitle());
		card.setDescription(request.getDescription());
		card.setPosition(request.getPosition() != null ? request.getPosition() : 0);
		card.setList(list);

		return CardResponse.from(cardRepository.save(card));
	}

	private KanbanList requireOwnedList(UUID listId) {
		UUID ownerId = currentUserId();
		KanbanList list = kanbanListRepository.findById(listId)
			.orElseThrow(() -> new NotFoundException("List not found"));

		if (!list.getOwnerId().equals(ownerId)) {
			throw new ForbiddenException("You are not the owner of this list");
		}
		return list;
	}

	private UUID currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
			throw new UnauthorizedException();
		}
		return userId;
	}
}
