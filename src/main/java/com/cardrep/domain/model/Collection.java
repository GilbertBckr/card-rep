package com.cardrep.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A set of Decks and child Collections, forming a tree structure.
 * Entity - identified by its unique CollectionID.
 * Aggregate Root - owns its child collections and deck references.
 * A Collection's name must be unique within its parent collection.
 */
public class Collection {

    private final String id;
    private String name;
    private final List<Collection> childCollections;
    private final List<Deck> childDecks;

    public Collection(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Collection ID must not be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Collection name must not be empty");
        }
        this.id = id;
        this.name = name;
        this.childCollections = new ArrayList<>();
        this.childDecks = new ArrayList<>();
    }

    public static Collection create(String name) {
        return new Collection(UUID.randomUUID().toString(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Collection> getChildCollections() {
        return Collections.unmodifiableList(childCollections);
    }

    public List<Deck> getChildDecks() {
        return Collections.unmodifiableList(childDecks);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Collection name must not be empty");
        }
        this.name = name;
    }

    public void addChildCollection(Collection child) {
        if (child == null) {
            throw new IllegalArgumentException("Child collection must not be null");
        }
        if (childCollections.stream().anyMatch(c -> c.getName().equalsIgnoreCase(child.getName()))) {
            throw new IllegalArgumentException(
                    "A collection with name '" + child.getName() + "' already exists in this collection");
        }
        childCollections.add(child);
    }

    public void removeChildCollection(String collectionId) {
        boolean removed = childCollections.removeIf(c -> c.getId().equals(collectionId));
        if (!removed) {
            throw new IllegalArgumentException("Child collection not found: " + collectionId);
        }
    }

    public void addDeck(Deck deck) {
        if (deck == null) {
            throw new IllegalArgumentException("Deck must not be null");
        }
        if (childDecks.stream().anyMatch(d -> d.getName().equalsIgnoreCase(deck.getName()))) {
            throw new IllegalArgumentException(
                    "A deck with name '" + deck.getName() + "' already exists in this collection");
        }
        childDecks.add(deck);
    }

    public void removeDeck(String deckId) {
        boolean removed = childDecks.removeIf(d -> d.getId().equals(deckId));
        if (!removed) {
            throw new IllegalArgumentException("Deck not found in collection: " + deckId);
        }
    }

    public Collection findChildCollection(String collectionId) {
        return childCollections.stream()
                .filter(c -> c.getId().equals(collectionId))
                .findFirst()
                .orElse(null);
    }

    public Deck findDeck(String deckId) {
        return childDecks.stream()
                .filter(d -> d.getId().equals(deckId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a name is unique among child collections.
     */
    public boolean isCollectionNameUnique(String candidateName) {
        return childCollections.stream()
                .noneMatch(c -> c.getName().equalsIgnoreCase(candidateName));
    }

    /**
     * Check if a deck name is unique within this collection.
     */
    public boolean isDeckNameUnique(String candidateName) {
        return childDecks.stream()
                .noneMatch(d -> d.getName().equalsIgnoreCase(candidateName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Collection{id='" + id + "', name='" + name + "', collections="
                + childCollections.size() + ", decks=" + childDecks.size() + "}";
    }
}
