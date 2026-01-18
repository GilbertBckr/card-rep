package com.cardrep.domain.model;

import java.util.Objects;

/**
 * Aggregated statistics about the learning progress of cards in a deck.
 * Value Object - immutable, compared by value.
 */
public class DeckStats {

    private final int totalCards;
    private final int cardsReviewed;
    private final int cardsEasy;
    private final int cardsMedium;
    private final int cardsHard;
    private final int cardsFailed;

    public DeckStats() {
        this(0, 0, 0, 0, 0, 0);
    }

    public DeckStats(int totalCards, int cardsReviewed, int cardsEasy, int cardsMedium,
                     int cardsHard, int cardsFailed) {
        this.totalCards = totalCards;
        this.cardsReviewed = cardsReviewed;
        this.cardsEasy = cardsEasy;
        this.cardsMedium = cardsMedium;
        this.cardsHard = cardsHard;
        this.cardsFailed = cardsFailed;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public int getCardsReviewed() {
        return cardsReviewed;
    }

    public int getCardsNotReviewed() {
        return totalCards - cardsReviewed;
    }

    public int getCardsEasy() {
        return cardsEasy;
    }

    public int getCardsMedium() {
        return cardsMedium;
    }

    public int getCardsHard() {
        return cardsHard;
    }

    public int getCardsFailed() {
        return cardsFailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeckStats deckStats = (DeckStats) o;
        return totalCards == deckStats.totalCards
                && cardsReviewed == deckStats.cardsReviewed
                && cardsEasy == deckStats.cardsEasy
                && cardsMedium == deckStats.cardsMedium
                && cardsHard == deckStats.cardsHard
                && cardsFailed == deckStats.cardsFailed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCards, cardsReviewed, cardsEasy, cardsMedium, cardsHard, cardsFailed);
    }

    @Override
    public String toString() {
        return String.format("DeckStats{total=%d, reviewed=%d, easy=%d, medium=%d, hard=%d, failed=%d}",
                totalCards, cardsReviewed, cardsEasy, cardsMedium, cardsHard, cardsFailed);
    }
}
