package com.cardrep.domain.model;

public interface DeckStatsObserver {

    void onDeckStatsChanged(Deck deck, DeckStats stats);
}
