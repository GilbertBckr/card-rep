package com.cardrep.application.deck;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;

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
