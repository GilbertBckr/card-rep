package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.KeyInput;
import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.application.deck.NextCardUseCase;
import com.cardrep.application.learning.LearnCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.Difficulty;

/**
 * TUI screen for interactive flashcard learning session.
 * Shows cards, handles flipping, and collects difficulty ratings.
 */
public class TuiLearningSession {

    private final TerminalUI terminal;
    private final TuiHelper helper;
    private final NextCardUseCase nextCardUseCase;
    private final LearnCardUseCase learnCardUseCase;

    public TuiLearningSession(TerminalUI terminal, TuiHelper helper,
                              NextCardUseCase nextCardUseCase, LearnCardUseCase learnCardUseCase) {
        this.terminal = terminal;
        this.helper = helper;
        this.nextCardUseCase = nextCardUseCase;
        this.learnCardUseCase = learnCardUseCase;
    }

    /**
     * Run a learning session for the given deck.
     *
     * @param deck the deck to learn from
     */
    public void run(Deck deck) {
        int cardsReviewed = 0;
        boolean learning = true;

        while (learning) {
            try {
                Card card = nextCardUseCase.execute(deck.getId());
                boolean showingBack = false;

                // Card display loop (front -> flip -> rate)
                while (true) {
                    drawCardScreen(deck, card, showingBack, cardsReviewed);

                    KeyInput key = terminal.readKey();

                    if (key.isBack() || key.isEof()) {
                        learning = false;
                        break;
                    }

                    if (!showingBack) {
                        // Showing front - Enter to flip
                        if (key.isSelect()) {
                            showingBack = true;
                        }
                    } else {
                        // Showing back - rate with 1-4
                        Difficulty difficulty = getDifficultyFromKey(key);
                        if (difficulty != null) {
                            learnCardUseCase.execute(card.getId(), deck.getId(), difficulty);
                            cardsReviewed++;
                            break; // Move to next card
                        }
                    }
                }

            } catch (IllegalStateException e) {
                // No more cards
                helper.showMessage("No more cards available in this deck.");
                learning = false;
            } catch (IllegalArgumentException e) {
                helper.showMessage("Error: " + e.getMessage());
                learning = false;
            }
        }

        // Show session summary
        if (cardsReviewed > 0) {
            helper.showMessage("Session complete! Reviewed " + cardsReviewed + " cards.");
        }
    }

    /**
     * Draw the card screen.
     */
    private void drawCardScreen(Deck deck, Card card, boolean showingBack, int cardsReviewed) {
        terminal.clear();
        TerminalSize size = terminal.getSize();

        // Title
        String title = "Learning: " + deck.getName();
        terminal.drawBox(0, 0, size.columns(), size.rows(), title);

        // Card box dimensions
        int cardWidth = Math.min(50, size.columns() - 8);
        int cardHeight = 7;
        int cardCol = (size.columns() - cardWidth) / 2;
        int cardRow = 3;

        // Draw card box
        terminal.drawBox(cardCol, cardRow, cardWidth, cardHeight, null);

        // Card content
        String frontText = truncateForCard(card.getFront().getText(), cardWidth - 4);
        int textCol = cardCol + (cardWidth - frontText.length()) / 2;
        terminal.drawText(textCol, cardRow + 2, frontText);

        if (showingBack) {
            // Draw separator line
            String separator = "─".repeat(cardWidth - 4);
            terminal.drawText(cardCol + 2, cardRow + 3, separator);

            // Draw back content
            String backText = truncateForCard(card.getBack().getText(), cardWidth - 4);
            int backCol = cardCol + (cardWidth - backText.length()) / 2;
            terminal.drawText(backCol, cardRow + 4, backText);
        } else {
            // Show flip instruction
            String flipText = "[Press Enter to flip]";
            int flipCol = cardCol + (cardWidth - flipText.length()) / 2;
            terminal.drawText(flipCol, cardRow + 4, flipText);
        }

        // Stats line
        int statsRow = cardRow + cardHeight + 1;
        String stats = String.format("Session: %d reviewed", cardsReviewed);
        terminal.drawText(cardCol, statsRow, stats);

        // Help line
        int helpRow = size.rows() - 2;
        if (showingBack) {
            terminal.drawText(2, helpRow, "Rate: [1] Easy  [2] Medium  [3] Hard  [4] Again    [q] End session");
        } else {
            terminal.drawText(2, helpRow, "[Enter] Flip card    [q] End session");
        }

        terminal.refresh();
    }

    /**
     * Get difficulty from key press.
     */
    private Difficulty getDifficultyFromKey(KeyInput key) {
        if (key.isDigit(1)) return Difficulty.EASY;
        if (key.isDigit(2)) return Difficulty.MEDIUM;
        if (key.isDigit(3)) return Difficulty.HARD;
        if (key.isDigit(4)) return Difficulty.AGAIN;
        return null;
    }

    /**
     * Truncate text to fit within card width.
     */
    private String truncateForCard(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - 3) + "...";
    }
}
