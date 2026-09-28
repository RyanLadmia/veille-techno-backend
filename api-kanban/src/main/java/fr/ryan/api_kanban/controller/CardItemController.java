package fr.ryan.api_kanban.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ryan.api_kanban.dto.CardResponse;
import fr.ryan.api_kanban.service.CardService;

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
}
