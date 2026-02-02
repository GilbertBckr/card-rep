package com.cardrep.infrastructure.persistence;

import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of DeckRepository.
 * Stores decks in a HashMap for fast lookup by ID.
 */
public class InMemoryDeckRepository implements DeckRepository {

    private final Map<String, Deck> decks = new HashMap<>();

    @Override
    public Deck save(Deck deck) {
        decks.put(deck.getId(), deck);
        return deck;
    }

    @Override
    public Optional<Deck> findById(String id) {
        return Optional.ofNullable(decks.get(id));
    }

    @Override
    public List<Deck> findAll() {
        return new ArrayList<>(decks.values());
    }

    @Override
    public void deleteById(String id) {
        decks.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return decks.containsKey(id);
    }
}
