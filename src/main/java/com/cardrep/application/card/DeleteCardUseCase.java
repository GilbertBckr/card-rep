package com.cardrep.application.card;

import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.DeckRepository;

/**
 * Use Case: Deletion of Card.
 * The card and its associated stats are deleted, and it is removed from its deck.
 */
public class DeleteCardUseCase {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public DeleteCardUseCase(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public void execute(String cardId, String deckId) {
        if (!cardRepository.existsById(cardId)) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        deck.removeCard(cardId);
        deckRepository.save(deck);
        cardRepository.deleteById(cardId);
    }
}
