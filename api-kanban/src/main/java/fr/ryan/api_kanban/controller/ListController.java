package fr.ryan.api_kanban.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ryan.api_kanban.dto.ListResponse;
import fr.ryan.api_kanban.service.ListService;

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
}
