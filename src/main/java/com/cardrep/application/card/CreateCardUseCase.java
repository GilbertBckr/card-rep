package com.cardrep.application.card;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.DeckRepository;

/**
 * Use Case: Creation of Card.
 * The user creates a card with front and back content, assigned to a specific deck.
 */
public class CreateCardUseCase {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CreateCardUseCase(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public Card execute(String deckId, CardContent front, CardContent back) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        Card card = Card.create(front, back);
        cardRepository.save(card);
        deck.addCard(card);
        deckRepository.save(deck);

        return card;
    }
}
