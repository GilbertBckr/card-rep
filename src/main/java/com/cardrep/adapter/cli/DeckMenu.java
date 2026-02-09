package com.cardrep.adapter.cli;

import com.cardrep.application.deck.CreateDeckUseCase;
import com.cardrep.application.deck.DeleteDeckUseCase;
import com.cardrep.application.deck.ModifyDeckUseCase;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.model.DeckStatsObserver;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;
import com.cardrep.infrastructure.algorithm.RandomRepetitionAlgorithm;
import com.cardrep.infrastructure.algorithm.SpacedRepetitionAlgorithm;

import java.util.List;
import java.util.Scanner;

/**
 * CLI menu for managing decks (create, modify, delete, view stats).
 *
 * NOTE: This class intentionally has some code duplication with CardMenu
 * (e.g., selectFromList patterns) for later DRY refactoring demonstration.
 */
public class DeckMenu {

    private final Scanner scanner;
    private final CreateDeckUseCase createDeckUseCase;
    private final ModifyDeckUseCase modifyDeckUseCase;
    private final DeleteDeckUseCase deleteDeckUseCase;
    private final CollectionRepository collectionRepository;
    private final RandomRepetitionAlgorithm randomAlgorithm;
    private final SpacedRepetitionAlgorithm spacedAlgorithm;
    private final DeckStatsObserver statsObserver;

    public DeckMenu(Scanner scanner, CreateDeckUseCase createDeckUseCase,
                    ModifyDeckUseCase modifyDeckUseCase, DeleteDeckUseCase deleteDeckUseCase,
                    CollectionRepository collectionRepository,
                    RandomRepetitionAlgorithm randomAlgorithm,
                    SpacedRepetitionAlgorithm spacedAlgorithm,
                    DeckStatsObserver statsObserver) {
        this.scanner = scanner;
        this.createDeckUseCase = createDeckUseCase;
        this.modifyDeckUseCase = modifyDeckUseCase;
        this.deleteDeckUseCase = deleteDeckUseCase;
        this.collectionRepository = collectionRepository;
        this.randomAlgorithm = randomAlgorithm;
        this.spacedAlgorithm = spacedAlgorithm;
        this.statsObserver = statsObserver;
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Deck Management ---");
            System.out.println("1. Create Deck");
            System.out.println("2. Modify Deck");
            System.out.println("3. Delete Deck");
            System.out.println("4. View Deck Stats");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createDeck();
                case "2" -> modifyDeck();
                case "3" -> deleteDeck();
                case "4" -> viewDeckStats();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createDeck() {
        String collectionId = selectCollection();
        if (collectionId == null) return;

        System.out.print("Enter deck name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Deck name cannot be empty.");
            return;
        }

        RepetitionAlgorithm algorithm = selectAlgorithm();
        if (algorithm == null) return;

        try {
            Deck deck = createDeckUseCase.execute(collectionId, name, algorithm);
            deck.addObserver(statsObserver);
            System.out.println("Deck created: " + deck.getName() + " (id: " + deck.getId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void modifyDeck() {
        String collectionId = selectCollection();
        if (collectionId == null) return;

        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) return;

        String deckId = selectDeckFromCollection(collection);
        if (deckId == null) return;

        System.out.print("Enter new name (or press Enter to keep current): ");
        String newName = scanner.nextLine().trim();

        System.out.println("Change algorithm? (y/n): ");
        String changeAlgo = scanner.nextLine().trim();
        RepetitionAlgorithm newAlgorithm = null;
        if (changeAlgo.equalsIgnoreCase("y")) {
            newAlgorithm = selectAlgorithm();
        }

        try {
            modifyDeckUseCase.execute(deckId, newName.isEmpty() ? null : newName, newAlgorithm);
            System.out.println("Deck modified successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteDeck() {
        String collectionId = selectCollection();
        if (collectionId == null) return;

        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) return;

        String deckId = selectDeckFromCollection(collection);
        if (deckId == null) return;

        try {
            deleteDeckUseCase.execute(deckId, collectionId);
            System.out.println("Deck and all its cards deleted successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewDeckStats() {
        String collectionId = selectCollection();
        if (collectionId == null) return;

        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) return;

        String deckId = selectDeckFromCollection(collection);
        if (deckId == null) return;

        Deck deck = collection.findDeck(deckId);
        if (deck == null) {
            System.out.println("Deck not found.");
            return;
        }

        DeckStats stats = deck.computeStats();
        System.out.println("\n--- Stats for '" + deck.getName() + "' ---");
        System.out.println("Total cards:    " + stats.getTotalCards());
        System.out.println("Reviewed:       " + stats.getCardsReviewed());
        System.out.println("Not reviewed:   " + stats.getCardsNotReviewed());
        System.out.println("Easy:           " + stats.getCardsEasy());
        System.out.println("Medium:         " + stats.getCardsMedium());
        System.out.println("Hard:           " + stats.getCardsHard());
        System.out.println("Failed (Again): " + stats.getCardsFailed());
        System.out.println("Algorithm:      " + deck.getRepetitionAlgorithm().getName());
    }

    private String selectCollection() {
        Collection root = collectionRepository.getRootCollection();
        System.out.println("\nCollections:");
        System.out.println("  1. " + root.getName() + " (Root)");

        List<Collection> children = root.getChildCollections();
        for (int i = 0; i < children.size(); i++) {
            System.out.println("  " + (i + 2) + ". " + children.get(i).getName());
        }
        System.out.print("Select collection (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index == 0) return root.getId();
            if (index > 0 && index <= children.size()) {
                return children.get(index - 1).getId();
            }
            System.out.println("Invalid selection.");
            return null;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private String selectDeckFromCollection(Collection collection) {
        List<Deck> decks = collection.getChildDecks();
        if (decks.isEmpty()) {
            System.out.println("No decks in this collection.");
            return null;
        }

        System.out.println("\nDecks:");
        for (int i = 0; i < decks.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + decks.get(i).getName());
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

    private RepetitionAlgorithm selectAlgorithm() {
        System.out.println("\nSelect scheduling algorithm:");
        System.out.println("  1. Spaced Repetition (recommended)");
        System.out.println("  2. Random");
        System.out.print("> ");

        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> spacedAlgorithm;
            case "2" -> randomAlgorithm;
            default -> {
                System.out.println("Invalid choice. Using Spaced Repetition.");
                yield spacedAlgorithm;
            }
        };
    }
}
