package com.cardrep.application.deck;

import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;

public class ModifyDeckUseCase {

    private final DeckRepository deckRepository;

    public ModifyDeckUseCase(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Deck execute(String deckId, String newName, RepetitionAlgorithm newAlgorithm) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck not found: " + deckId));

        if (newName != null && !newName.isBlank()) {
            deck.setName(newName);
        }
        if (newAlgorithm != null) {
            deck.setRepetitionAlgorithm(newAlgorithm);
        }

        return deckRepository.save(deck);
    }
}
