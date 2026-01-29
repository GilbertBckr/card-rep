package com.cardrep.application.learning;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.Difficulty;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.DeckRepository;

public class LearnCardUseCase {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public LearnCardUseCase(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public Card execute(String cardId, String deckId, Difficulty difficulty) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        card.recordReview(difficulty);
        cardRepository.save(card);

        deck.notifyObservers();

        return card;
    }
}
