package com.cardrep.domain.repository;

import com.cardrep.domain.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {

    Deck save(Deck deck);

    Optional<Deck> findById(String id);

    List<Deck> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
