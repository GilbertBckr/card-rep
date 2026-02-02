package com.cardrep.infrastructure.persistence;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.repository.CardRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of CardRepository.
 * Stores cards in a HashMap for fast lookup by ID.
 */
public class InMemoryCardRepository implements CardRepository {

    private final Map<String, Card> cards = new HashMap<>();

    @Override
    public Card save(Card card) {
        cards.put(card.getId(), card);
        return card;
    }

    @Override
    public Optional<Card> findById(String id) {
        return Optional.ofNullable(cards.get(id));
    }

    @Override
    public List<Card> findAll() {
        return new ArrayList<>(cards.values());
    }

    @Override
    public void deleteById(String id) {
        cards.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return cards.containsKey(id);
    }
}
