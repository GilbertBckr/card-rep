package com.cardrep.application.collection;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;

/**
 * Use Case: Deletion of Collection.
 * A Collection can be deleted, deleting all its child content (decks, collections)
 * recursively.
 */
public class DeleteCollectionUseCase {

    private final CollectionRepository collectionRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeleteCollectionUseCase(CollectionRepository collectionRepository,
                                   DeckRepository deckRepository,
                                   CardRepository cardRepository) {
        this.collectionRepository = collectionRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    public void execute(String collectionId, String parentCollectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        // Recursively delete child collections
        for (Collection child : collection.getChildCollections()) {
            deleteRecursively(child);
        }

        // Delete all decks and their cards
        for (Deck deck : collection.getChildDecks()) {
            for (Card card : deck.getCards()) {
                cardRepository.deleteById(card.getId());
            }
            deckRepository.deleteById(deck.getId());
        }

        // Remove from parent
        Collection parent = collectionRepository.findById(parentCollectionId)
                .orElseThrow(() -> new IllegalArgumentException("Parent collection not found: " + parentCollectionId));
        parent.removeChildCollection(collectionId);
        collectionRepository.save(parent);

        collectionRepository.deleteById(collectionId);
    }

    private void deleteRecursively(Collection collection) {
        for (Collection child : collection.getChildCollections()) {
            deleteRecursively(child);
        }
        for (Deck deck : collection.getChildDecks()) {
            for (Card card : deck.getCards()) {
                cardRepository.deleteById(card.getId());
            }
            deckRepository.deleteById(deck.getId());
        }
        collectionRepository.deleteById(collection.getId());
    }
}
