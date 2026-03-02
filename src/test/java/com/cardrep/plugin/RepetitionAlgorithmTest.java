package com.cardrep.plugin;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.model.Difficulty;
import com.cardrep.plugin.algorithm.SpacedRepetitionAlgorithm;
import com.cardrep.plugin.algorithm.RandomRepetitionAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RepetitionAlgorithm strategy implementations.
 */
class RepetitionAlgorithmTest {

    @Test
    void spacedRepetition_shouldPrioritizeUnreviewedCards() {
        SpacedRepetitionAlgorithm algorithm = new SpacedRepetitionAlgorithm();

        Card reviewed = Card.create(new CardContent("Reviewed Q"), new CardContent("A"));
        reviewed.recordReview(Difficulty.EASY);

        Card unreviewed = Card.create(new CardContent("New Q"), new CardContent("A"));

        Card result = algorithm.selectNextCard(Arrays.asList(reviewed, unreviewed));

        assertEquals(unreviewed, result);
    }

    @Test
    void spacedRepetition_shouldPrioritizeFailedCards() {
        SpacedRepetitionAlgorithm algorithm = new SpacedRepetitionAlgorithm();

        Card easy = Card.create(new CardContent("Easy"), new CardContent("A"));
        easy.recordReview(Difficulty.EASY);

        Card failed = Card.create(new CardContent("Failed"), new CardContent("A"));
        failed.recordReview(Difficulty.AGAIN);

        Card result = algorithm.selectNextCard(Arrays.asList(easy, failed));

        assertEquals(failed, result);
    }

    @Test
    void spacedRepetition_emptyList_shouldReturnNull() {
        SpacedRepetitionAlgorithm algorithm = new SpacedRepetitionAlgorithm();

        assertNull(algorithm.selectNextCard(Collections.emptyList()));
    }

    @Test
    void spacedRepetition_nullList_shouldReturnNull() {
        SpacedRepetitionAlgorithm algorithm = new SpacedRepetitionAlgorithm();

        assertNull(algorithm.selectNextCard(null));
    }

    @Test
    void randomRepetition_shouldReturnACard() {
        RandomRepetitionAlgorithm algorithm = new RandomRepetitionAlgorithm(new Random(42));

        Card card1 = Card.create(new CardContent("Q1"), new CardContent("A1"));
        Card card2 = Card.create(new CardContent("Q2"), new CardContent("A2"));

        Card result = algorithm.selectNextCard(Arrays.asList(card1, card2));

        assertNotNull(result);
        assertTrue(result.equals(card1) || result.equals(card2));
    }

    @Test
    void randomRepetition_emptyList_shouldReturnNull() {
        RandomRepetitionAlgorithm algorithm = new RandomRepetitionAlgorithm();

        assertNull(algorithm.selectNextCard(Collections.emptyList()));
    }

    @Test
    void spacedRepetition_getName_shouldReturnCorrectName() {
        assertEquals("Spaced Repetition", new SpacedRepetitionAlgorithm().getName());
    }

    @Test
    void randomRepetition_getName_shouldReturnCorrectName() {
        assertEquals("Random", new RandomRepetitionAlgorithm().getName());
    }
}
