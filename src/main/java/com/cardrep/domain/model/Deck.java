package com.cardrep.domain.model;

import com.cardrep.domain.service.RepetitionAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A set of Cards with a scheduling algorithm for learning.
 * Entity - identified by its unique DeckID.
 * Aggregate Root - owns its cards (deleting a deck deletes all its cards).
 */
public class Deck {

    private final String id;
    private String name;
    private final List<Card> cards;
    private RepetitionAlgorithm repetitionAlgorithm;
    private final List<DeckStatsObserver> observers;

    public Deck(String id, String name, RepetitionAlgorithm repetitionAlgorithm) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Deck ID must not be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deck name must not be empty");
        }
        if (repetitionAlgorithm == null) {
            throw new IllegalArgumentException("Repetition algorithm must not be null");
        }
        this.id = id;
        this.name = name;
        this.cards = new ArrayList<>();
        this.repetitionAlgorithm = repetitionAlgorithm;
        this.observers = new ArrayList<>();
    }

    public static Deck create(String name, RepetitionAlgorithm repetitionAlgorithm) {
        return new Deck(UUID.randomUUID().toString(), name, repetitionAlgorithm);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public RepetitionAlgorithm getRepetitionAlgorithm() {
        return repetitionAlgorithm;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deck name must not be empty");
        }
        this.name = name;
    }

    public void setRepetitionAlgorithm(RepetitionAlgorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Repetition algorithm must not be null");
        }
        this.repetitionAlgorithm = algorithm;
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card must not be null");
        }
        if (cards.stream().anyMatch(c -> c.getId().equals(card.getId()))) {
            throw new IllegalArgumentException("Card already exists in this deck");
        }
        cards.add(card);
        notifyObservers();
    }

    public void removeCard(String cardId) {
        boolean removed = cards.removeIf(c -> c.getId().equals(cardId));
        if (!removed) {
            throw new IllegalArgumentException("Card not found in deck: " + cardId);
        }
        notifyObservers();
    }

    public Card getCardById(String cardId) {
        return cards.stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the next card to learn based on the deck's repetition algorithm.
     */
    public Card getNextCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return repetitionAlgorithm.selectNextCard(cards);
    }

    /**
     * Compute aggregate deck stats from the current cards.
     * Refactored: extracted helper methods for clarity (Extract Method refactoring).
     */
    public DeckStats computeStats() {
        int total = cards.size();
        List<Card> reviewedCards = getReviewedCards();
        int reviewed = reviewedCards.size();

        return new DeckStats(
                total,
                reviewed,
                countByLastDifficulty(reviewedCards, Difficulty.EASY),
                countByLastDifficulty(reviewedCards, Difficulty.MEDIUM),
                countByLastDifficulty(reviewedCards, Difficulty.HARD),
                countByLastDifficulty(reviewedCards, Difficulty.AGAIN)
        );
    }

    /**
     * Returns all cards that have been reviewed at least once.
     */
    private List<Card> getReviewedCards() {
        return cards.stream()
                .filter(card -> card.getStats().hasBeenReviewed())
                .toList();
    }

    /**
     * Counts how many of the given cards have the specified last difficulty.
     */
    private int countByLastDifficulty(List<Card> reviewedCards, Difficulty difficulty) {
        return (int) reviewedCards.stream()
                .filter(card -> card.getStats().getLastDifficulty() == difficulty)
                .count();
    }

    // Observer pattern support
    public void addObserver(DeckStatsObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DeckStatsObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        DeckStats stats = computeStats();
        for (DeckStatsObserver observer : observers) {
            observer.onDeckStatsChanged(this, stats);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return Objects.equals(id, deck.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Deck{id='" + id + "', name='" + name + "', cards=" + cards.size() + "}";
    }
}
