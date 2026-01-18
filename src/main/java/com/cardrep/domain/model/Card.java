package com.cardrep.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * The smallest unit of learnable content, consisting of a front and a back side.
 * Entity - identified by its unique CardID.
 */
public class Card {

    private final String id;
    private CardContent front;
    private CardContent back;
    private CardStats stats;

    public Card(String id, CardContent front, CardContent back) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card ID must not be empty");
        }
        if (front == null) {
            throw new IllegalArgumentException("Card front must not be null");
        }
        if (back == null) {
            throw new IllegalArgumentException("Card back must not be null");
        }
        this.id = id;
        this.front = front;
        this.back = back;
        this.stats = new CardStats();
    }

    public static Card create(CardContent front, CardContent back) {
        return new Card(UUID.randomUUID().toString(), front, back);
    }

    public String getId() {
        return id;
    }

    public CardContent getFront() {
        return front;
    }

    public CardContent getBack() {
        return back;
    }

    public CardStats getStats() {
        return stats;
    }

    /**
     * Modify the front and back of the card. Stats remain unchanged.
     */
    public void modify(CardContent newFront, CardContent newBack) {
        if (newFront == null) {
            throw new IllegalArgumentException("Card front must not be null");
        }
        if (newBack == null) {
            throw new IllegalArgumentException("Card back must not be null");
        }
        this.front = newFront;
        this.back = newBack;
    }

    /**
     * Record a review with the given difficulty rating.
     */
    public void recordReview(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty must not be null");
        }
        this.stats = stats.withNewReview(difficulty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{id='" + id + "', front=" + front + "}";
    }
}
