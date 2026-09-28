package fr.ryan.api_kanban.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ryan.api_kanban.dto.CardResponse;
import fr.ryan.api_kanban.dto.CreateCardRequest;
import fr.ryan.api_kanban.service.CardService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lists/{listId}/cards")
public class CardController {

	private final CardService cardService;

	public CardController(CardService cardService) {
		this.cardService = cardService;
	}

	@GetMapping
	public ResponseEntity<List<CardResponse>> getCardsForList(@PathVariable UUID listId) {
		return ResponseEntity.ok(cardService.getCardsForList(listId));
	}

	@PostMapping
	public ResponseEntity<CardResponse> createCard(
			@PathVariable UUID listId,
			@Valid @RequestBody CreateCardRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(listId, request));
	}
}
