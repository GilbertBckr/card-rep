package com.cardrep.application.deck;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;

/**
 * Use Case: Next Card Function.
 * For a given Deck the user can request the next card to learn,
 * based on the deck's chosen scheduling algorithm.
 */
public class NextCardUseCase {

    private final DeckRepository deckRepository;

    public NextCardUseCase(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Card execute(String deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        Card nextCard = deck.getNextCard();
        if (nextCard == null) {
            throw new IllegalStateException("No cards available in deck: " + deck.getName());
        }

        return nextCard;
    }
}
