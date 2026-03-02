package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.application.deck.CreateDeckUseCase;
import com.cardrep.application.deck.DeleteDeckUseCase;
import com.cardrep.application.deck.ModifyDeckUseCase;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * TUI menu for managing decks (create, modify, delete, view stats).
 */
public class TuiDeckMenu {

    private final TerminalUI terminal;
    private final TuiHelper helper;
    private final CreateDeckUseCase createDeckUseCase;
    private final ModifyDeckUseCase modifyDeckUseCase;
    private final DeleteDeckUseCase deleteDeckUseCase;
    private final CollectionRepository collectionRepository;
    private final DeckRepository deckRepository;
    private final RepetitionAlgorithm spacedAlgorithm;
    private final RepetitionAlgorithm randomAlgorithm;
    private final TuiCollectionMenu collectionMenu;

    public TuiDeckMenu(TerminalUI terminal, TuiHelper helper,
                       CreateDeckUseCase createDeckUseCase,
                       ModifyDeckUseCase modifyDeckUseCase,
                       DeleteDeckUseCase deleteDeckUseCase,
                       CollectionRepository collectionRepository,
                       DeckRepository deckRepository,
                       RepetitionAlgorithm spacedAlgorithm,
                       RepetitionAlgorithm randomAlgorithm,
                       TuiCollectionMenu collectionMenu) {
        this.terminal = terminal;
        this.helper = helper;
        this.createDeckUseCase = createDeckUseCase;
        this.modifyDeckUseCase = modifyDeckUseCase;
        this.deleteDeckUseCase = deleteDeckUseCase;
        this.collectionRepository = collectionRepository;
        this.deckRepository = deckRepository;
        this.spacedAlgorithm = spacedAlgorithm;
        this.randomAlgorithm = randomAlgorithm;
        this.collectionMenu = collectionMenu;
    }

    /**
     * Run the deck management menu.
     */
    public void run() {
        boolean running = true;
        while (running) {
            List<String> options = List.of(
                    "Create Deck",
                    "Rename Deck",
                    "Delete Deck",
                    "View Deck Stats",
                    "Back to Main Menu"
            );

            int selected = helper.selectFromList(options, 2, 2, "Deck Management");

            switch (selected) {
                case 0 -> createDeck();
                case 1 -> modifyDeck();
                case 2 -> deleteDeck();
                case 3 -> viewDeckStats();
                case 4, -1 -> running = false;
            }
        }
    }

    private void createDeck() {
        // Select collection
        String collectionId = collectionMenu.selectCollection("Select collection for new deck:");
        if (collectionId == null) return;

        // Get deck name
        String name = helper.readText("Create Deck", "Enter deck name:");
        if (name == null || name.isBlank()) {
            helper.showError("Deck name cannot be empty.");
            return;
        }

        // Select algorithm
        RepetitionAlgorithm algorithm = selectAlgorithm();
        if (algorithm == null) return;

        try {
            Deck deck = createDeckUseCase.execute(collectionId, name, algorithm);
            helper.showMessage("Deck created: " + deck.getName());
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void modifyDeck() {
        // Select deck
        DeckSelection selection = selectDeck("Select deck to rename:");
        if (selection == null) return;

        // Get new name
        String newName = helper.readText("Rename Deck", "Enter new name (or leave empty to keep current):");
        if (newName == null) return;

        // Ask about algorithm change
        RepetitionAlgorithm newAlgorithm = null;
        if (helper.confirm("Change the repetition algorithm?")) {
            newAlgorithm = selectAlgorithm();
        }

        try {
            modifyDeckUseCase.execute(selection.deckId, newName.isBlank() ? null : newName, newAlgorithm);
            helper.showMessage("Deck modified successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void deleteDeck() {
        // Select deck
        DeckSelection selection = selectDeck("Select deck to delete:");
        if (selection == null) return;

        // Confirm deletion
        if (!helper.confirm("Delete this deck and all its cards?")) {
            helper.showMessage("Deletion cancelled.");
            return;
        }

        try {
            deleteDeckUseCase.execute(selection.deckId, selection.collectionId);
            helper.showMessage("Deck deleted successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void viewDeckStats() {
        // Select deck
        DeckSelection selection = selectDeck("Select deck to view stats:");
        if (selection == null) return;

        Deck deck = deckRepository.findById(selection.deckId).orElse(null);
        if (deck == null) {
            helper.showError("Deck not found.");
            return;
        }

        DeckStats stats = deck.computeStats();
        showStats(deck, stats);
    }

    private void showStats(Deck deck, DeckStats stats) {
        TerminalSize size = terminal.getSize();

        terminal.clear();
        terminal.drawBox(0, 0, size.columns(), size.rows(), "Stats: " + deck.getName());

        int row = 3;
        int col = 4;

        terminal.drawText(col, row++, "Algorithm:      " + deck.getRepetitionAlgorithm().getName());
        row++;
        terminal.drawText(col, row++, "Total cards:    " + stats.getTotalCards());
        terminal.drawText(col, row++, "Reviewed:       " + stats.getCardsReviewed());
        terminal.drawText(col, row++, "Not reviewed:   " + stats.getCardsNotReviewed());
        row++;
        terminal.drawText(col, row++, "Easy:           " + stats.getCardsEasy());
        terminal.drawText(col, row++, "Medium:         " + stats.getCardsMedium());
        terminal.drawText(col, row++, "Hard:           " + stats.getCardsHard());
        terminal.drawText(col, row++, "Failed (Again): " + stats.getCardsFailed());

        terminal.drawText(col, size.rows() - 2, "[Press any key to continue]");
        terminal.refresh();

        terminal.readKey();
    }

    private RepetitionAlgorithm selectAlgorithm() {
        List<String> options = List.of(
                "Spaced Repetition (recommended)",
                "Random"
        );

        int selected = helper.selectFromList(options, 2, 2, "Select repetition algorithm:");

        return switch (selected) {
            case 0 -> spacedAlgorithm;
            case 1 -> randomAlgorithm;
            default -> null;
        };
    }

    /**
     * Select a deck and return both deck ID and containing collection ID.
     */
    public DeckSelection selectDeck(String title) {
        List<Deck> decks = new ArrayList<>();
        List<String> collectionIds = new ArrayList<>();
        List<String> displayItems = new ArrayList<>();

        Collection root = collectionRepository.getRootCollection();
        collectAllDecks(root, decks, collectionIds, displayItems, 0);

        if (decks.isEmpty()) {
            helper.showMessage("No decks available. Create a deck first.");
            return null;
        }

        int selected = helper.selectFromList(displayItems, 2, 2, title);
        if (selected == -1) return null;

        return new DeckSelection(decks.get(selected).getId(), collectionIds.get(selected));
    }

    /**
     * Select a deck and return just the deck ID.
     */
    public String selectDeckId(String title) {
        DeckSelection selection = selectDeck(title);
        return selection != null ? selection.deckId : null;
    }

    private void collectAllDecks(Collection collection, List<Deck> decks,
                                 List<String> collectionIds, List<String> displayItems, int depth) {
        String indent = "  ".repeat(depth);

        for (Deck deck : collection.getChildDecks()) {
            DeckStats stats = deck.computeStats();
            String display = String.format("%s%s (%d cards)",
                    indent, deck.getName(), stats.getTotalCards());
            displayItems.add(display);
            decks.add(deck);
            collectionIds.add(collection.getId());
        }

        for (Collection child : collection.getChildCollections()) {
            collectAllDecks(child, decks, collectionIds, displayItems, depth + 1);
        }
    }

    /**
     * Holds both deck ID and its parent collection ID.
     */
    public record DeckSelection(String deckId, String collectionId) {}
}
