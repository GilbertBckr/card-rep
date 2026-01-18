package com.cardrep.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Statistics about a specific card's learning history.
 * Value Object - immutable, compared by value.
 * Stores a series of review entries (timestamp, difficulty rating).
 */
public class CardStats {

    private final List<ReviewEntry> reviewHistory;

    public CardStats() {
        this.reviewHistory = new ArrayList<>();
    }

    private CardStats(List<ReviewEntry> reviewHistory) {
        this.reviewHistory = new ArrayList<>(reviewHistory);
    }

    /**
     * Creates a new CardStats with an additional review entry.
     * Value Object pattern: returns new instance instead of mutating.
     */
    public CardStats withNewReview(Difficulty difficulty) {
        CardStats updated = new CardStats(this.reviewHistory);
        updated.reviewHistory.add(new ReviewEntry(LocalDateTime.now(), difficulty));
        return updated;
    }

    public List<ReviewEntry> getReviewHistory() {
        return Collections.unmodifiableList(reviewHistory);
    }

    public int getTotalReviews() {
        return reviewHistory.size();
    }

    public boolean hasBeenReviewed() {
        return !reviewHistory.isEmpty();
    }

    public Difficulty getLastDifficulty() {
        if (reviewHistory.isEmpty()) {
            return null;
        }
        return reviewHistory.get(reviewHistory.size() - 1).getDifficulty();
    }

    public LocalDateTime getLastReviewTime() {
        if (reviewHistory.isEmpty()) {
            return null;
        }
        return reviewHistory.get(reviewHistory.size() - 1).getTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardStats cardStats = (CardStats) o;
        return Objects.equals(reviewHistory, cardStats.reviewHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewHistory);
    }

    /**
     * A single review entry recording when a card was reviewed and how it was rated.
     */
    public static class ReviewEntry {
        private final LocalDateTime timestamp;
        private final Difficulty difficulty;

        public ReviewEntry(LocalDateTime timestamp, Difficulty difficulty) {
            this.timestamp = timestamp;
            this.difficulty = difficulty;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Difficulty getDifficulty() {
            return difficulty;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReviewEntry that = (ReviewEntry) o;
            return Objects.equals(timestamp, that.timestamp) && difficulty == that.difficulty;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, difficulty);
        }
    }
}
