package com.cardrep.application.card;

import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.repository.CardRepository;

public class ModifyCardUseCase {

    private final CardRepository cardRepository;

    public ModifyCardUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card execute(String cardId, CardContent newFront, CardContent newBack) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        card.modify(newFront, newBack);
        return cardRepository.save(card);
    }
}
