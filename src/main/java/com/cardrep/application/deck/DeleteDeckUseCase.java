package com.cardrep.application.deck;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;

/**
 * Use Case: Deletion of Deck.
 * When a deck is deleted, all of its child cards are deleted as well,
 * since a card must belong to at least one deck.
 */
public class DeleteDeckUseCase {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CollectionRepository collectionRepository;

    public DeleteDeckUseCase(DeckRepository deckRepository, CardRepository cardRepository,
                             CollectionRepository collectionRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.collectionRepository = collectionRepository;
    }

    public void execute(String deckId, String collectionId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        // Delete all cards in the deck
        for (Card card : deck.getCards()) {
            cardRepository.deleteById(card.getId());
        }

        // Remove deck from parent collection
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));
        collection.removeDeck(deckId);
        collectionRepository.save(collection);

        deckRepository.deleteById(deckId);
    }
}
