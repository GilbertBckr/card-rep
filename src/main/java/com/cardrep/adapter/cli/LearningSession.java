package com.cardrep.adapter.cli;

import com.cardrep.application.deck.NextCardUseCase;
import com.cardrep.application.learning.LearnCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.Difficulty;
import com.cardrep.domain.repository.DeckRepository;

import java.util.List;
import java.util.Scanner;

/**
 * Handles the interactive learning session where the user reviews cards.
 * Shows the front, reveals the back, and lets the user rate difficulty.
 */
public class LearningSession {

    private final Scanner scanner;
    private final NextCardUseCase nextCardUseCase;
    private final LearnCardUseCase learnCardUseCase;
    private final DeckRepository deckRepository;

    public LearningSession(Scanner scanner, NextCardUseCase nextCardUseCase,
                           LearnCardUseCase learnCardUseCase, DeckRepository deckRepository) {
        this.scanner = scanner;
        this.nextCardUseCase = nextCardUseCase;
        this.learnCardUseCase = learnCardUseCase;
        this.deckRepository = deckRepository;
    }

    public void run() {
        String deckId = selectDeck();
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

    private String selectDeck() {
        List<Deck> decks = deckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No decks available. Create a deck and add cards first.");
            return null;
        }

        System.out.println("\nAvailable decks:");
        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            System.out.println("  " + (i + 1) + ". " + deck.getName()
                    + " (" + deck.getCards().size() + " cards)");
        }
        System.out.print("Select deck (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= decks.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return decks.get(index).getId();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        return text + " ".repeat(length - text.length());
    }
}
