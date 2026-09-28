package fr.ryan.api_kanban.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.ryan.api_kanban.dto.CardResponse;
import fr.ryan.api_kanban.dto.UpdateCardRequest;
import fr.ryan.api_kanban.service.CardService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CardItemController {

	private final CardService cardService;

	public CardItemController(CardService cardService) {
		this.cardService = cardService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
		return ResponseEntity.ok(cardService.getCard(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<CardResponse> updateCard(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateCardRequest request) {
		return ResponseEntity.ok(cardService.updateCard(id, request));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCard(@PathVariable UUID id) {
		cardService.deleteCard(id);
	}
}
