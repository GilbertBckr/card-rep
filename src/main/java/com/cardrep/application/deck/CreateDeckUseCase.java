package com.cardrep.application.deck;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;

/**
 * Use Case: Creation of Deck.
 * A new Deck is created inside a specific collection with a unique name.
 * The user can choose the scheduling algorithm; otherwise a default is used.
 */
public class CreateDeckUseCase {

    private final DeckRepository deckRepository;
    private final CollectionRepository collectionRepository;

    public CreateDeckUseCase(DeckRepository deckRepository, CollectionRepository collectionRepository) {
        this.deckRepository = deckRepository;
        this.collectionRepository = collectionRepository;
    }

    public Deck execute(String collectionId, String name, RepetitionAlgorithm algorithm) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        if (!collection.isDeckNameUnique(name)) {
            throw new IllegalArgumentException(
                    "A deck with name '" + name + "' already exists in this collection");
        }

        Deck deck = Deck.create(name, algorithm);
        deckRepository.save(deck);
        collection.addDeck(deck);
        collectionRepository.save(collection);

        return deck;
    }
}
