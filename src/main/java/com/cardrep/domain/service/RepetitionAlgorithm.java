package com.cardrep.domain.service;

import com.cardrep.domain.model.Card;

import java.util.List;

/**
 * Strategy interface for selecting the next card to learn from a list of cards.
 * Design Pattern: Strategy - allows different scheduling algorithms to be used interchangeably.
 */
public interface RepetitionAlgorithm {

    /**
     * Selects the next card that should be shown to the user for learning.
     *
     * @param cards the list of available cards in the deck
     * @return the next card to study, or null if no cards available
     */
    Card selectNextCard(List<Card> cards);

    /**
     * Returns the name of this algorithm for display purposes.
     */
    String getName();
}
