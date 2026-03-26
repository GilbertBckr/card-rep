package com.cardrep.adapter.cli;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

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

    public String selectDeck() {
        List<Deck> decks = deckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No decks available. Create a deck first.");
            return null;
        }
        return selectFromList(decks, "Available decks", Deck::getName, Deck::getId);
    }

    public String selectCollection() {
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
}
