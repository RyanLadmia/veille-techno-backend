package fr.ryan.api_kanban.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.ryan.api_kanban.entity.KanbanList;

public interface KanbanListRepository extends JpaRepository<KanbanList, UUID> {

	List<KanbanList> findByOwner_Id(UUID ownerId);
}
