package com.cardrep.domain.repository;

import com.cardrep.domain.model.Deck;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Deck persistence.
 * Defined in the domain layer - implementations reside in infrastructure.
 */
public interface DeckRepository {

    Deck save(Deck deck);

    Optional<Deck> findById(String id);

    List<Deck> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
