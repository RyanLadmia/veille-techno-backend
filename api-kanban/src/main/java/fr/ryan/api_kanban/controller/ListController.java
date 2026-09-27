package fr.ryan.api_kanban.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.ryan.api_kanban.dto.CreateListRequest;
import fr.ryan.api_kanban.dto.ListResponse;
import fr.ryan.api_kanban.dto.UpdateListRequest;
import fr.ryan.api_kanban.service.ListService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lists")
public class ListController {

	private final ListService listService;

	public ListController(ListService listService) {
		this.listService = listService;
	}

	@GetMapping
	public ResponseEntity<List<ListResponse>> getMyLists() {
		return ResponseEntity.ok(listService.getMyLists());
	}

	@PostMapping
	public ResponseEntity<ListResponse> createList(@Valid @RequestBody CreateListRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(listService.createList(request));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ListResponse> updateList(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateListRequest request) {
		return ResponseEntity.ok(listService.updateList(id, request));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteList(@PathVariable UUID id) {
		listService.deleteList(id);
	}
}
