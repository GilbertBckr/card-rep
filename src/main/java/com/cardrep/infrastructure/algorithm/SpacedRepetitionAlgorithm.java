package com.cardrep.infrastructure.algorithm;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Difficulty;
import com.cardrep.domain.service.RepetitionAlgorithm;

import java.util.List;

/**
 * A spaced repetition algorithm that prioritizes cards based on their review history.
 * Strategy Pattern: concrete strategy implementation.
 *
 * Priority order:
 * 1. Cards never reviewed (new cards first)
 * 2. Cards rated AGAIN (failed - need immediate review)
 * 3. Cards rated HARD
 * 4. Cards rated MEDIUM
 * 5. Cards rated EASY (lowest priority)
 */
public class SpacedRepetitionAlgorithm implements RepetitionAlgorithm {

    @Override
    public Card selectNextCard(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }

        Card bestCard = null;
        int bestPriority = Integer.MAX_VALUE;

        for (Card card : cards) {
            int priority = calculatePriority(card);
            if (priority < bestPriority) {
                bestPriority = priority;
                bestCard = card;
            }
        }

        return bestCard;
    }

    private int calculatePriority(Card card) {
        if (!card.getStats().hasBeenReviewed()) {
            return 0; // Highest priority: never reviewed
        }

        Difficulty lastDifficulty = card.getStats().getLastDifficulty();
        if (lastDifficulty == Difficulty.AGAIN) return 1;
        if (lastDifficulty == Difficulty.HARD) return 2;
        if (lastDifficulty == Difficulty.MEDIUM) return 3;
        if (lastDifficulty == Difficulty.EASY) return 4;

        return 5;
    }

    @Override
    public String getName() {
        return "Spaced Repetition";
    }
}
