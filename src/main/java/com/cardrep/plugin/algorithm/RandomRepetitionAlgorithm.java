package com.cardrep.plugin.algorithm;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.service.RepetitionAlgorithm;

import java.util.List;
import java.util.Random;

/**
 * A simple random repetition algorithm that selects cards randomly.
 * Strategy Pattern: concrete strategy implementation.
 */
public class RandomRepetitionAlgorithm implements RepetitionAlgorithm {

    private final Random random;

    public RandomRepetitionAlgorithm() {
        this.random = new Random();
    }

    public RandomRepetitionAlgorithm(Random random) {
        this.random = random;
    }

    @Override
    public Card selectNextCard(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        int index = random.nextInt(cards.size());
        return cards.get(index);
    }

    @Override
    public String getName() {
        return "Random";
    }
}
