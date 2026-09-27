# API Kanban (Spring Boot)

API REST pour un tableau Kanban (utilisateurs, listes, cartes).

- Documentation interactive : [`/api`](http://localhost:3310/api)
- Contrat OpenAPI : [`openapi.yaml`](./openapi.yaml)

## Choix d'implémentation

### Suppression d'une liste et ses cartes

`DELETE /api/lists/{id}` **supprime en cascade** toutes les cartes de la liste.

Ce comportement est volontaire : une liste sans ses cartes n'a pas de sens métier dans ce Kanban, et l'entité JPA `KanbanList` mappe déjà les cartes avec `CascadeType.ALL` + `orphanRemoval`.

Alternative écartée : refuser la suppression si la liste n'est pas vide (409) — non retenue pour rester simple côté client (un seul appel suffit).
