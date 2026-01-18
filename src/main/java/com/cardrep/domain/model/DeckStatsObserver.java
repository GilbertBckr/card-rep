package com.cardrep.domain.model;

/**
 * Observer interface for Deck statistics changes.
 * Design Pattern: Observer - allows decoupled notification when deck stats change.
 */
public interface DeckStatsObserver {

    /**
     * Called when the statistics of a deck have changed.
     *
     * @param deck  the deck whose stats changed
     * @param stats the updated deck statistics
     */
    void onDeckStatsChanged(Deck deck, DeckStats stats);
}
