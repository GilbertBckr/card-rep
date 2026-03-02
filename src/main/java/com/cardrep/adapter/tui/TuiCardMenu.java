package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.application.card.CreateCardUseCase;
import com.cardrep.application.card.DeleteCardUseCase;
import com.cardrep.application.card.ModifyCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.DeckRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * TUI menu for managing cards (create, modify, delete, list).
 */
public class TuiCardMenu {

    private final TerminalUI terminal;
    private final TuiHelper helper;
    private final CreateCardUseCase createCardUseCase;
    private final ModifyCardUseCase modifyCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final DeckRepository deckRepository;
    private final TuiDeckMenu deckMenu;

    public TuiCardMenu(TerminalUI terminal, TuiHelper helper,
                       CreateCardUseCase createCardUseCase,
                       ModifyCardUseCase modifyCardUseCase,
                       DeleteCardUseCase deleteCardUseCase,
                       DeckRepository deckRepository,
                       TuiDeckMenu deckMenu) {
        this.terminal = terminal;
        this.helper = helper;
        this.createCardUseCase = createCardUseCase;
        this.modifyCardUseCase = modifyCardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;
        this.deckRepository = deckRepository;
        this.deckMenu = deckMenu;
    }

    /**
     * Run the card management menu.
     */
    public void run() {
        boolean running = true;
        while (running) {
            List<String> options = List.of(
                    "Create Card",
                    "Modify Card",
                    "Delete Card",
                    "List Cards in Deck",
                    "Back to Main Menu"
            );

            int selected = helper.selectFromList(options, 2, 2, "Card Management");

            switch (selected) {
                case 0 -> createCard();
                case 1 -> modifyCard();
                case 2 -> deleteCard();
                case 3 -> listCards();
                case 4, -1 -> running = false;
            }
        }
    }

    private void createCard() {
        // Select deck
        String deckId = deckMenu.selectDeckId("Select deck for new card:");
        if (deckId == null) return;

        // Get front text
        String frontText = helper.readText("Create Card", "Enter front text:");
        if (frontText == null || frontText.isBlank()) {
            helper.showError("Front text cannot be empty.");
            return;
        }

        // Get back text
        String backText = helper.readText("Create Card", "Enter back text:");
        if (backText == null || backText.isBlank()) {
            helper.showError("Back text cannot be empty.");
            return;
        }

        try {
            CardContent front = new CardContent(frontText);
            CardContent back = new CardContent(backText);
            Card card = createCardUseCase.execute(deckId, front, back);
            helper.showMessage("Card created successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void modifyCard() {
        // Select deck
        String deckId = deckMenu.selectDeckId("Select deck containing the card:");
        if (deckId == null) return;

        // Select card
        CardSelection selection = selectCard(deckId, "Select card to modify:");
        if (selection == null) return;

        // Get new front text
        String frontText = helper.readText("Modify Card", "Enter new front text:");
        if (frontText == null || frontText.isBlank()) {
            helper.showError("Front text cannot be empty.");
            return;
        }

        // Get new back text
        String backText = helper.readText("Modify Card", "Enter new back text:");
        if (backText == null || backText.isBlank()) {
            helper.showError("Back text cannot be empty.");
            return;
        }

        try {
            CardContent front = new CardContent(frontText);
            CardContent back = new CardContent(backText);
            modifyCardUseCase.execute(selection.cardId, front, back);
            helper.showMessage("Card modified successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void deleteCard() {
        // Select deck
        String deckId = deckMenu.selectDeckId("Select deck containing the card:");
        if (deckId == null) return;

        // Select card
        CardSelection selection = selectCard(deckId, "Select card to delete:");
        if (selection == null) return;

        // Confirm deletion
        if (!helper.confirm("Delete this card?")) {
            helper.showMessage("Deletion cancelled.");
            return;
        }

        try {
            deleteCardUseCase.execute(selection.cardId, deckId);
            helper.showMessage("Card deleted successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void listCards() {
        // Select deck
        String deckId = deckMenu.selectDeckId("Select deck to view cards:");
        if (deckId == null) return;

        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null) {
            helper.showError("Deck not found.");
            return;
        }

        List<Card> cards = deck.getCards();
        if (cards.isEmpty()) {
            helper.showMessage("No cards in this deck.");
            return;
        }

        showCardList(deck, cards);
    }

    private void showCardList(Deck deck, List<Card> cards) {
        TerminalSize size = terminal.getSize();

        terminal.clear();
        terminal.drawBox(0, 0, size.columns(), size.rows(), "Cards in: " + deck.getName());

        int row = 3;
        int col = 2;
        int maxWidth = size.columns() - 6;

        for (int i = 0; i < cards.size() && row < size.rows() - 3; i++) {
            Card card = cards.get(i);
            String frontText = truncate(card.getFront().getText(), 20);
            String backText = truncate(card.getBack().getText(), 20);
            String line = String.format("%2d. Front: %-20s | Back: %-20s | Reviews: %d",
                    i + 1, frontText, backText, card.getStats().getTotalReviews());
            terminal.drawText(col, row++, truncate(line, maxWidth));
        }

        if (cards.size() > size.rows() - 6) {
            terminal.drawText(col, row, "... and " + (cards.size() - (size.rows() - 6)) + " more");
        }

        terminal.drawText(col, size.rows() - 2, "[Press any key to continue]");
        terminal.refresh();

        terminal.readKey();
    }

    private CardSelection selectCard(String deckId, String title) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null || deck.getCards().isEmpty()) {
            helper.showMessage("No cards available in this deck.");
            return null;
        }

        List<Card> cards = deck.getCards();
        List<String> displayItems = new ArrayList<>();

        for (Card card : cards) {
            String display = String.format("Front: %s | Back: %s",
                    truncate(card.getFront().getText(), 25),
                    truncate(card.getBack().getText(), 25));
            displayItems.add(display);
        }

        int selected = helper.selectFromList(displayItems, 2, 2, title);
        if (selected == -1) return null;

        return new CardSelection(cards.get(selected).getId(), deckId);
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Holds card ID and its parent deck ID.
     */
    private record CardSelection(String cardId, String deckId) {}
}
