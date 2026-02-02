package com.cardrep.infrastructure.observer;

import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.model.DeckStatsObserver;

/**
 * Observer that logs deck statistics changes to the console.
 * Design Pattern: Observer - concrete observer implementation.
 */
public class DeckStatsLogger implements DeckStatsObserver {

    @Override
    public void onDeckStatsChanged(Deck deck, DeckStats stats) {
        System.out.println("[Stats Update] Deck '" + deck.getName() + "': " + stats);
    }
}
