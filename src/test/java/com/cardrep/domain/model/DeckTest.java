package com.cardrep.domain.model;

import com.cardrep.domain.service.RepetitionAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Deck entity.
 */
class DeckTest {

    private RepetitionAlgorithm dummyAlgorithm;

    @BeforeEach
    void setUp() {
        // Simple algorithm that always returns the first card
        dummyAlgorithm = new RepetitionAlgorithm() {
            @Override
            public Card selectNextCard(List<Card> cards) {
                return cards.isEmpty() ? null : cards.get(0);
            }

            @Override
            public String getName() {
                return "Dummy";
            }
        };
    }

    @Test
    void createDeck_withValidParams_shouldSucceed() {
        Deck deck = Deck.create("My Deck", dummyAlgorithm);

        assertNotNull(deck.getId());
        assertEquals("My Deck", deck.getName());
        assertTrue(deck.getCards().isEmpty());
        assertEquals(dummyAlgorithm, deck.getRepetitionAlgorithm());
    }

    @Test
    void createDeck_withNullName_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> Deck.create(null, dummyAlgorithm));
    }

    @Test
    void createDeck_withNullAlgorithm_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> Deck.create("Deck", null));
    }

    @Test
    void addCard_shouldIncreaseDeckSize() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));

        deck.addCard(card);

        assertEquals(1, deck.getCards().size());
        assertEquals(card, deck.getCards().get(0));
    }

    @Test
    void addCard_duplicateId_shouldThrow() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        deck.addCard(card);

        assertThrows(IllegalArgumentException.class, () -> deck.addCard(card));
    }

    @Test
    void removeCard_existingCard_shouldDecreaseDeckSize() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        deck.addCard(card);

        deck.removeCard(card.getId());

        assertTrue(deck.getCards().isEmpty());
    }

    @Test
    void removeCard_nonExistentCard_shouldThrow() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);

        assertThrows(IllegalArgumentException.class,
                () -> deck.removeCard("non-existent-id"));
    }

    @Test
    void getNextCard_shouldDelegateToAlgorithm() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        deck.addCard(card);

        Card nextCard = deck.getNextCard();

        assertEquals(card, nextCard);
    }

    @Test
    void getNextCard_emptyDeck_shouldReturnNull() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);

        assertNull(deck.getNextCard());
    }

    @Test
    void computeStats_withReviewedCards_shouldAggregateCorrectly() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);

        Card card1 = Card.create(new CardContent("Q1"), new CardContent("A1"));
        card1.recordReview(Difficulty.EASY);
        Card card2 = Card.create(new CardContent("Q2"), new CardContent("A2"));
        card2.recordReview(Difficulty.HARD);
        Card card3 = Card.create(new CardContent("Q3"), new CardContent("A3"));

        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);

        DeckStats stats = deck.computeStats();

        assertEquals(3, stats.getTotalCards());
        assertEquals(2, stats.getCardsReviewed());
        assertEquals(1, stats.getCardsNotReviewed());
        assertEquals(1, stats.getCardsEasy());
        assertEquals(1, stats.getCardsHard());
    }

    @Test
    void observer_shouldBeNotifiedOnCardAdd() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));

        final boolean[] notified = {false};
        deck.addObserver((d, stats) -> notified[0] = true);

        deck.addCard(card);

        assertTrue(notified[0]);
    }

    @Test
    void setName_withValidName_shouldUpdate() {
        Deck deck = Deck.create("Old Name", dummyAlgorithm);

        deck.setName("New Name");

        assertEquals("New Name", deck.getName());
    }

    @Test
    void setName_withBlankName_shouldThrow() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);

        assertThrows(IllegalArgumentException.class, () -> deck.setName(""));
    }
}
