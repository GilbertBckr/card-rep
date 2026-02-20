package com.cardrep.adapter.cli;

import com.cardrep.application.deck.NextCardUseCase;
import com.cardrep.application.learning.LearnCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Difficulty;

import java.util.Scanner;

/**
 * Handles the interactive learning session where the user reviews cards.
 * Shows the front, reveals the back, and lets the user rate difficulty.
 * Uses MenuHelper for shared selection logic (DRY refactoring).
 */
public class LearningSession {

    private final Scanner scanner;
    private final NextCardUseCase nextCardUseCase;
    private final LearnCardUseCase learnCardUseCase;
    private final MenuHelper menuHelper;

    public LearningSession(Scanner scanner, NextCardUseCase nextCardUseCase,
                           LearnCardUseCase learnCardUseCase, MenuHelper menuHelper) {
        this.scanner = scanner;
        this.nextCardUseCase = nextCardUseCase;
        this.learnCardUseCase = learnCardUseCase;
        this.menuHelper = menuHelper;
    }

    public void run() {
        String deckId = menuHelper.selectDeck();
        if (deckId == null) return;

        System.out.println("\n=== Learning Session Started ===");
        System.out.println("Press Enter to reveal the back. Type 'quit' to stop.\n");

        boolean learning = true;
        while (learning) {
            try {
                Card card = nextCardUseCase.execute(deckId);

                // Show front
                System.out.println("┌─────────────────────────────┐");
                System.out.println("│ FRONT:                      │");
                System.out.println("│ " + padRight(card.getFront().getText(), 28) + "│");
                System.out.println("└─────────────────────────────┘");

                System.out.print("Press Enter to reveal (or 'quit'): ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit")) {
                    learning = false;
                    continue;
                }

                // Show back
                System.out.println("┌─────────────────────────────┐");
                System.out.println("│ BACK:                       │");
                System.out.println("│ " + padRight(card.getBack().getText(), 28) + "│");
                System.out.println("└─────────────────────────────┘");

                // Rate difficulty
                Difficulty difficulty = rateDifficulty();
                if (difficulty == null) {
                    learning = false;
                    continue;
                }

                learnCardUseCase.execute(card.getId(), deckId, difficulty);
                System.out.println("Rated: " + difficulty + "\n");

            } catch (IllegalStateException e) {
                System.out.println("No more cards available: " + e.getMessage());
                learning = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                learning = false;
            }
        }

        System.out.println("=== Learning Session Ended ===");
    }

    private Difficulty rateDifficulty() {
        System.out.println("\nRate difficulty:");
        System.out.println("  1. Easy");
        System.out.println("  2. Medium");
        System.out.println("  3. Hard");
        System.out.println("  4. Again (failed)");
        System.out.println("  0. Quit session");
        System.out.print("> ");

        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> Difficulty.EASY;
            case "2" -> Difficulty.MEDIUM;
            case "3" -> Difficulty.HARD;
            case "4" -> Difficulty.AGAIN;
            case "0" -> null;
            default -> {
                System.out.println("Invalid choice. Defaulting to Medium.");
                yield Difficulty.MEDIUM;
            }
        };
    }

    private String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        return text + " ".repeat(length - text.length());
    }
}
