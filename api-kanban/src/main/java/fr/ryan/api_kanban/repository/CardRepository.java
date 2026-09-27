package fr.ryan.api_kanban.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.ryan.api_kanban.entity.Card;

public interface CardRepository extends JpaRepository<Card, UUID> {

	List<Card> findByList_Id(UUID listId);
}
