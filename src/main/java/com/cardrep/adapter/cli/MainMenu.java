package com.cardrep.adapter.cli;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.repository.CollectionRepository;

import java.util.Scanner;

/**
 * Main menu of the CLI application.
 * Provides navigation to sub-menus for cards, decks, collections, and learning.
 */
public class MainMenu {

    private final Scanner scanner;
    private final CardMenu cardMenu;
    private final DeckMenu deckMenu;
    private final CollectionMenu collectionMenu;
    private final LearningSession learningSession;
    private final CollectionRepository collectionRepository;

    public MainMenu(Scanner scanner, CardMenu cardMenu, DeckMenu deckMenu,
                    CollectionMenu collectionMenu, LearningSession learningSession,
                    CollectionRepository collectionRepository) {
        this.scanner = scanner;
        this.cardMenu = cardMenu;
        this.deckMenu = deckMenu;
        this.collectionMenu = collectionMenu;
        this.learningSession = learningSession;
        this.collectionRepository = collectionRepository;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showOverview();
                case "2" -> collectionMenu.run();
                case "3" -> deckMenu.run();
                case "4" -> cardMenu.run();
                case "5" -> learningSession.run();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Overview (browse collections)");
        System.out.println("2. Manage Collections");
        System.out.println("3. Manage Decks");
        System.out.println("4. Manage Cards");
        System.out.println("5. Start Learning Session");
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private void showOverview() {
        Collection root = collectionRepository.getRootCollection();
        System.out.println("\n--- Collection Overview ---");
        printCollectionTree(root, 0);
    }

    private void printCollectionTree(Collection collection, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "[Collection] " + collection.getName()
                + " (id: " + collection.getId() + ")");

        for (Deck deck : collection.getChildDecks()) {
            DeckStats stats = deck.computeStats();
            System.out.println(indent + "  [Deck] " + deck.getName()
                    + " (" + stats.getTotalCards() + " cards, "
                    + stats.getCardsReviewed() + " reviewed)"
                    + " - Algorithm: " + deck.getRepetitionAlgorithm().getName());
        }

        for (Collection child : collection.getChildCollections()) {
            printCollectionTree(child, depth + 1);
        }
    }
}
