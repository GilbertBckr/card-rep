package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.TerminalSize;
import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.model.DeckStats;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu of the TUI application.
 * Provides navigation to all features: learning, collections, decks, and cards.
 */
public class TuiMainMenu {

    private final TerminalUI terminal;
    private final TuiHelper helper;
    private final TuiLearningSession learningSession;
    private final TuiCollectionMenu collectionMenu;
    private final TuiDeckMenu deckMenu;
    private final TuiCardMenu cardMenu;
    private final CollectionRepository collectionRepository;
    private final DeckRepository deckRepository;

    public TuiMainMenu(TerminalUI terminal, TuiHelper helper,
                       TuiLearningSession learningSession,
                       TuiCollectionMenu collectionMenu,
                       TuiDeckMenu deckMenu,
                       TuiCardMenu cardMenu,
                       CollectionRepository collectionRepository,
                       DeckRepository deckRepository) {
        this.terminal = terminal;
        this.helper = helper;
        this.learningSession = learningSession;
        this.collectionMenu = collectionMenu;
        this.deckMenu = deckMenu;
        this.cardMenu = cardMenu;
        this.collectionRepository = collectionRepository;
        this.deckRepository = deckRepository;
    }

    /**
     * Run the main menu loop.
     */
    public void run() {
        boolean running = true;

        while (running) {
            List<String> options = List.of(
                    "Start Learning Session",
                    "Browse Collections",
                    "Manage Collections",
                    "Manage Decks",
                    "Manage Cards",
                    "Exit"
            );

            int selected = helper.selectFromList(options, 2, 2, "Card Repetition - Main Menu");

            switch (selected) {
                case 0 -> startLearning();
                case 1 -> browseCollections();
                case 2 -> collectionMenu.run();
                case 3 -> deckMenu.run();
                case 4 -> cardMenu.run();
                case 5, -1 -> running = false;
            }
        }
    }

    /**
     * Start a learning session by selecting a deck.
     */
    private void startLearning() {
        List<Deck> decks = getAllDecks();

        if (decks.isEmpty()) {
            helper.showMessage("No decks available. Create a deck first.");
            return;
        }

        List<String> menuItems = buildDeckMenuItems(decks);
        int selected = helper.selectFromList(menuItems, 2, 2, "Select a deck to learn:");

        if (selected >= 0 && selected < decks.size()) {
            Deck selectedDeck = decks.get(selected);
            learningSession.run(selectedDeck);
        }
    }

    /**
     * Browse the collection tree.
     */
    private void browseCollections() {
        TerminalSize size = terminal.getSize();

        terminal.clear();
        terminal.drawBox(0, 0, size.columns(), size.rows(), "Collection Overview");

        Collection root = collectionRepository.getRootCollection();
        int row = 3;
        row = drawCollectionTree(root, 2, row, 0, size.rows() - 4);

        terminal.drawText(2, size.rows() - 2, "[Press any key to continue]");
        terminal.refresh();

        terminal.readKey();
    }

    /**
     * Draw the collection tree recursively.
     */
    private int drawCollectionTree(Collection collection, int col, int row, int depth, int maxRow) {
        if (row >= maxRow) return row;

        String indent = "  ".repeat(depth);
        String prefix = depth == 0 ? "[Root] " : "[Collection] ";
        terminal.drawText(col, row++, indent + prefix + collection.getName());

        // Draw decks in this collection
        for (Deck deck : collection.getChildDecks()) {
            if (row >= maxRow) return row;
            DeckStats stats = deck.computeStats();
            String deckLine = String.format("%s  [Deck] %s (%d cards, %d reviewed)",
                    indent, deck.getName(), stats.getTotalCards(), stats.getCardsReviewed());
            terminal.drawText(col, row++, deckLine);
        }

        // Draw child collections
        for (Collection child : collection.getChildCollections()) {
            row = drawCollectionTree(child, col, row, depth + 1, maxRow);
        }

        return row;
    }

    /**
     * Get all decks from all collections.
     */
    private List<Deck> getAllDecks() {
        List<Deck> allDecks = new ArrayList<>();
        Collection root = collectionRepository.getRootCollection();
        collectDecks(root, allDecks);
        return allDecks;
    }

    /**
     * Recursively collect all decks from a collection tree.
     */
    private void collectDecks(Collection collection, List<Deck> decks) {
        decks.addAll(collection.getChildDecks());
        for (Collection child : collection.getChildCollections()) {
            collectDecks(child, decks);
        }
    }

    /**
     * Build menu item strings from decks.
     */
    private List<String> buildDeckMenuItems(List<Deck> decks) {
        List<String> items = new ArrayList<>();
        for (Deck deck : decks) {
            DeckStats stats = deck.computeStats();
            String item = String.format("%-25s (%d cards, %d reviewed)",
                    truncate(deck.getName(), 25),
                    stats.getTotalCards(),
                    stats.getCardsReviewed());
            items.add(item);
        }
        return items;
    }

    /**
     * Truncate text if longer than max length.
     */
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
