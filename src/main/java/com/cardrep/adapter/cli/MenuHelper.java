package com.cardrep.adapter.cli;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Shared helper for CLI menus to eliminate duplicated selection logic.
 * Provides reusable methods for selecting decks, collections, and generic list items.
 *
 * Refactoring: Extracted from CardMenu, DeckMenu, CollectionMenu, and LearningSession
 * to apply the DRY principle.
 */
public class MenuHelper {

    private final Scanner scanner;
    private final DeckRepository deckRepository;
    private final CollectionRepository collectionRepository;

    public MenuHelper(Scanner scanner, DeckRepository deckRepository,
                      CollectionRepository collectionRepository) {
        this.scanner = scanner;
        this.deckRepository = deckRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * Generic method to display a numbered list and let the user select an item.
     * Returns the ID of the selected item, or null if the selection is invalid.
     *
     * @param items       the list of items to display
     * @param header      the header text to display above the list
     * @param displayFunc function to convert an item to its display string
     * @param idFunc      function to extract the ID from an item
     * @param <T>         the type of items in the list
     * @return the ID of the selected item, or null
     */
    public <T> String selectFromList(List<T> items, String header,
                                     Function<T, String> displayFunc,
                                     Function<T, String> idFunc) {
        if (items.isEmpty()) {
            System.out.println("No items available.");
            return null;
        }

        System.out.println("\n" + header + ":");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + displayFunc.apply(items.get(i)));
        }
        System.out.print("Select (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= items.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return idFunc.apply(items.get(index));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    /**
     * Lets the user select a deck from all available decks.
     * Previously duplicated in CardMenu and LearningSession.
     */
    public String selectDeck() {
        List<Deck> decks = deckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No decks available. Create a deck first.");
            return null;
        }

        return selectFromList(decks, "Available decks",
                deck -> deck.getName() + " (" + deck.getCards().size() + " cards)",
                Deck::getId);
    }

    /**
     * Lets the user select a deck within a specific collection.
     * Previously in DeckMenu only.
     */
    public String selectDeckFromCollection(Collection collection) {
        List<Deck> decks = collection.getChildDecks();
        return selectFromList(decks, "Decks in '" + collection.getName() + "'",
                Deck::getName, Deck::getId);
    }

    /**
     * Lets the user select a collection (including root).
     * Previously duplicated in DeckMenu and CollectionMenu.
     */
    public String selectCollection() {
        Collection root = collectionRepository.getRootCollection();
        List<Collection> children = root.getChildCollections();

        // Build a combined list: root + children
        System.out.println("\nCollections:");
        System.out.println("  1. " + root.getName() + " (Root)");
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

    /**
     * Lets the user select a non-root collection.
     * Previously in CollectionMenu only.
     */
    public String selectNonRootCollection() {
        Collection root = collectionRepository.getRootCollection();
        List<Collection> children = root.getChildCollections();
        if (children.isEmpty()) {
            System.out.println("No non-root collections available.");
            return null;
        }

        return selectFromList(children, "Collections (excluding root)",
                Collection::getName, Collection::getId);
    }
}
