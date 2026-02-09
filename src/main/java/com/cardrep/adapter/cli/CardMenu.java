package com.cardrep.adapter.cli;

import com.cardrep.application.card.CreateCardUseCase;
import com.cardrep.application.card.DeleteCardUseCase;
import com.cardrep.application.card.ModifyCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;

import java.util.List;
import java.util.Scanner;

/**
 * CLI menu for managing cards (create, modify, delete).
 */
public class CardMenu {

    private final Scanner scanner;
    private final CreateCardUseCase createCardUseCase;
    private final ModifyCardUseCase modifyCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final DeckRepository deckRepository;

    public CardMenu(Scanner scanner, CreateCardUseCase createCardUseCase,
                    ModifyCardUseCase modifyCardUseCase, DeleteCardUseCase deleteCardUseCase,
                    DeckRepository deckRepository) {
        this.scanner = scanner;
        this.createCardUseCase = createCardUseCase;
        this.modifyCardUseCase = modifyCardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;
        this.deckRepository = deckRepository;
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Card Management ---");
            System.out.println("1. Create Card");
            System.out.println("2. Modify Card");
            System.out.println("3. Delete Card");
            System.out.println("4. List Cards in Deck");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createCard();
                case "2" -> modifyCard();
                case "3" -> deleteCard();
                case "4" -> listCards();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createCard() {
        String deckId = selectDeck();
        if (deckId == null) return;

        System.out.print("Enter front text: ");
        String frontText = scanner.nextLine().trim();
        if (frontText.isEmpty()) {
            System.out.println("Front text cannot be empty.");
            return;
        }

        System.out.print("Enter back text: ");
        String backText = scanner.nextLine().trim();
        if (backText.isEmpty()) {
            System.out.println("Back text cannot be empty.");
            return;
        }

        try {
            CardContent front = new CardContent(frontText);
            CardContent back = new CardContent(backText);
            Card card = createCardUseCase.execute(deckId, front, back);
            System.out.println("Card created: " + card.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void modifyCard() {
        String deckId = selectDeck();
        if (deckId == null) return;

        String cardId = selectCard(deckId);
        if (cardId == null) return;

        System.out.print("Enter new front text: ");
        String frontText = scanner.nextLine().trim();
        if (frontText.isEmpty()) {
            System.out.println("Front text cannot be empty.");
            return;
        }

        System.out.print("Enter new back text: ");
        String backText = scanner.nextLine().trim();
        if (backText.isEmpty()) {
            System.out.println("Back text cannot be empty.");
            return;
        }

        try {
            CardContent front = new CardContent(frontText);
            CardContent back = new CardContent(backText);
            modifyCardUseCase.execute(cardId, front, back);
            System.out.println("Card modified successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteCard() {
        String deckId = selectDeck();
        if (deckId == null) return;

        String cardId = selectCard(deckId);
        if (cardId == null) return;

        try {
            deleteCardUseCase.execute(cardId, deckId);
            System.out.println("Card deleted successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCards() {
        String deckId = selectDeck();
        if (deckId == null) return;

        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null) {
            System.out.println("Deck not found.");
            return;
        }

        List<Card> cards = deck.getCards();
        if (cards.isEmpty()) {
            System.out.println("No cards in this deck.");
            return;
        }

        System.out.println("\nCards in deck '" + deck.getName() + "':");
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            System.out.println("  " + (i + 1) + ". Front: " + card.getFront()
                    + " | Back: " + card.getBack()
                    + " | Reviews: " + card.getStats().getTotalReviews());
        }
    }

    private String selectDeck() {
        List<Deck> decks = deckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No decks available. Create a deck first.");
            return null;
        }

        System.out.println("\nAvailable decks:");
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

    private String selectCard(String deckId) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null || deck.getCards().isEmpty()) {
            System.out.println("No cards available in this deck.");
            return null;
        }

        List<Card> cards = deck.getCards();
        System.out.println("\nCards:");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cards.get(i).getFront());
        }
        System.out.print("Select card (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= cards.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return cards.get(index).getId();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }
}
