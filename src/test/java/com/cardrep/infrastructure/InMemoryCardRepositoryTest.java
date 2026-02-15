package com.cardrep.infrastructure;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.infrastructure.persistence.InMemoryCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InMemoryCardRepository (Fake implementation).
 */
class InMemoryCardRepositoryTest {

    private InMemoryCardRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCardRepository();
    }

    @Test
    void save_shouldStoreCard() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));

        Card saved = repository.save(card);

        assertEquals(card, saved);
        assertTrue(repository.existsById(card.getId()));
    }

    @Test
    void findById_existingCard_shouldReturnCard() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        repository.save(card);

        Optional<Card> found = repository.findById(card.getId());

        assertTrue(found.isPresent());
        assertEquals(card, found.get());
    }

    @Test
    void findById_nonExistingCard_shouldReturnEmpty() {
        Optional<Card> found = repository.findById("nonexistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllCards() {
        Card card1 = Card.create(new CardContent("Q1"), new CardContent("A1"));
        Card card2 = Card.create(new CardContent("Q2"), new CardContent("A2"));
        repository.save(card1);
        repository.save(card2);

        List<Card> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void deleteById_shouldRemoveCard() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        repository.save(card);

        repository.deleteById(card.getId());

        assertFalse(repository.existsById(card.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void existsById_shouldReturnCorrectBoolean() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        repository.save(card);

        assertTrue(repository.existsById(card.getId()));
        assertFalse(repository.existsById("no-such-id"));
    }
}
