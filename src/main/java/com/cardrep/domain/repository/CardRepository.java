package com.cardrep.domain.repository;

import com.cardrep.domain.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    Optional<Card> findById(String id);

    List<Card> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
