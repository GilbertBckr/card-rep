package com.cardrep.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Card entity.
 */
class CardTest {

    @Test
    void createCard_withValidContent_shouldSucceed() {
        CardContent front = new CardContent("What is Java?");
        CardContent back = new CardContent("A programming language");

        Card card = Card.create(front, back);

        assertNotNull(card.getId());
        assertEquals(front, card.getFront());
        assertEquals(back, card.getBack());
        assertNotNull(card.getStats());
        assertEquals(0, card.getStats().getTotalReviews());
    }

    @Test
    void createCard_withNullFront_shouldThrow() {
        CardContent back = new CardContent("Answer");

        assertThrows(IllegalArgumentException.class, () -> Card.create(null, back));
    }

    @Test
    void createCard_withNullBack_shouldThrow() {
        CardContent front = new CardContent("Question");

        assertThrows(IllegalArgumentException.class, () -> Card.create(front, null));
    }

    @Test
    void modifyCard_shouldUpdateContentButKeepStats() {
        Card card = Card.create(
                new CardContent("Old front"),
                new CardContent("Old back")
        );
        card.recordReview(Difficulty.EASY);
        int reviewsBefore = card.getStats().getTotalReviews();

        card.modify(
                new CardContent("New front"),
                new CardContent("New back")
        );

        assertEquals("New front", card.getFront().getText());
        assertEquals("New back", card.getBack().getText());
        assertEquals(reviewsBefore, card.getStats().getTotalReviews());
    }

    @Test
    void recordReview_shouldUpdateStats() {
        Card card = Card.create(
                new CardContent("Question"),
                new CardContent("Answer")
        );

        assertFalse(card.getStats().hasBeenReviewed());

        card.recordReview(Difficulty.HARD);

        assertTrue(card.getStats().hasBeenReviewed());
        assertEquals(1, card.getStats().getTotalReviews());
        assertEquals(Difficulty.HARD, card.getStats().getLastDifficulty());
    }

    @Test
    void recordReview_withNullDifficulty_shouldThrow() {
        Card card = Card.create(
                new CardContent("Question"),
                new CardContent("Answer")
        );

        assertThrows(IllegalArgumentException.class, () -> card.recordReview(null));
    }

    @Test
    void equals_sameId_shouldBeEqual() {
        Card card1 = new Card("test-id", new CardContent("Q"), new CardContent("A"));
        Card card2 = new Card("test-id", new CardContent("Different Q"), new CardContent("Different A"));

        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    void equals_differentId_shouldNotBeEqual() {
        Card card1 = Card.create(new CardContent("Q"), new CardContent("A"));
        Card card2 = Card.create(new CardContent("Q"), new CardContent("A"));

        assertNotEquals(card1, card2);
    }
}
