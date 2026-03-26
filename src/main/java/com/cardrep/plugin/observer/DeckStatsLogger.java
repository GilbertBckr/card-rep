package com.cardrep.plugin.observer;

import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.model.DeckStatsObserver;

public class DeckStatsLogger implements DeckStatsObserver {

    @Override
    public void onDeckStatsChanged(Deck deck, DeckStats stats) {
        System.out.println("[Stats Update] Deck '" + deck.getName() + "': " + stats);
    }
}
