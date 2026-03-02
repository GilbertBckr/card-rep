package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.application.card.CreateCardUseCase;
import com.cardrep.application.card.DeleteCardUseCase;
import com.cardrep.application.card.ModifyCardUseCase;
import com.cardrep.application.collection.CreateCollectionUseCase;
import com.cardrep.application.collection.DeleteCollectionUseCase;
import com.cardrep.application.collection.ModifyCollectionUseCase;
import com.cardrep.application.deck.CreateDeckUseCase;
import com.cardrep.application.deck.DeleteDeckUseCase;
import com.cardrep.application.deck.ModifyDeckUseCase;
import com.cardrep.application.deck.NextCardUseCase;
import com.cardrep.application.learning.LearnCardUseCase;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;

/**
 * Entry point for the TUI (Text User Interface) application.
 * Manages terminal lifecycle and wires all TUI components.
 */
public class CardRepTui {

    private final TerminalUI terminal;
    private final TuiMainMenu mainMenu;

    /**
     * Create a new TUI application instance with full CRUD support.
     *
     * @param terminal               the terminal UI implementation
     * @param collectionRepository   repository for collections
     * @param deckRepository         repository for decks
     * @param createCollectionUseCase use case for creating collections
     * @param modifyCollectionUseCase use case for modifying collections
     * @param deleteCollectionUseCase use case for deleting collections
     * @param createDeckUseCase      use case for creating decks
     * @param modifyDeckUseCase      use case for modifying decks
     * @param deleteDeckUseCase      use case for deleting decks
     * @param createCardUseCase      use case for creating cards
     * @param modifyCardUseCase      use case for modifying cards
     * @param deleteCardUseCase      use case for deleting cards
     * @param nextCardUseCase        use case for getting next card
     * @param learnCardUseCase       use case for recording learning
     * @param spacedAlgorithm        spaced repetition algorithm
     * @param randomAlgorithm        random repetition algorithm
     */
    public CardRepTui(TerminalUI terminal,
                      CollectionRepository collectionRepository,
                      DeckRepository deckRepository,
                      CreateCollectionUseCase createCollectionUseCase,
                      ModifyCollectionUseCase modifyCollectionUseCase,
                      DeleteCollectionUseCase deleteCollectionUseCase,
                      CreateDeckUseCase createDeckUseCase,
                      ModifyDeckUseCase modifyDeckUseCase,
                      DeleteDeckUseCase deleteDeckUseCase,
                      CreateCardUseCase createCardUseCase,
                      ModifyCardUseCase modifyCardUseCase,
                      DeleteCardUseCase deleteCardUseCase,
                      NextCardUseCase nextCardUseCase,
                      LearnCardUseCase learnCardUseCase,
                      RepetitionAlgorithm spacedAlgorithm,
                      RepetitionAlgorithm randomAlgorithm) {
        this.terminal = terminal;

        // Build helper and menus
        TuiHelper helper = new TuiHelper(terminal);

        // Learning session
        TuiLearningSession learningSession = new TuiLearningSession(
                terminal, helper, nextCardUseCase, learnCardUseCase);

        // Collection menu
        TuiCollectionMenu collectionMenu = new TuiCollectionMenu(
                terminal, helper,
                createCollectionUseCase, modifyCollectionUseCase, deleteCollectionUseCase,
                collectionRepository);

        // Deck menu (needs collection menu for selection)
        TuiDeckMenu deckMenu = new TuiDeckMenu(
                terminal, helper,
                createDeckUseCase, modifyDeckUseCase, deleteDeckUseCase,
                collectionRepository, deckRepository,
                spacedAlgorithm, randomAlgorithm,
                collectionMenu);

        // Card menu (needs deck menu for selection)
        TuiCardMenu cardMenu = new TuiCardMenu(
                terminal, helper,
                createCardUseCase, modifyCardUseCase, deleteCardUseCase,
                deckRepository, deckMenu);

        // Main menu
        this.mainMenu = new TuiMainMenu(
                terminal, helper,
                learningSession, collectionMenu, deckMenu, cardMenu,
                collectionRepository, deckRepository);
    }

    /**
     * Run the TUI application.
     * Initializes the terminal, runs the main menu, and cleans up on exit.
     */
    public void run() {
        try {
            terminal.start();
            mainMenu.run();
        } finally {
            terminal.stop();
        }
    }
}
