package com.cardrep.adapter.cli;

import com.cardrep.adapter.tui.CardRepTui;
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
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.plugin.algorithm.RandomRepetitionAlgorithm;
import com.cardrep.plugin.algorithm.SpacedRepetitionAlgorithm;
import com.cardrep.plugin.observer.DeckStatsLogger;
import com.cardrep.plugin.persistence.InMemoryCardRepository;
import com.cardrep.plugin.persistence.InMemoryCollectionRepository;
import com.cardrep.plugin.persistence.InMemoryDeckRepository;
import com.cardrep.plugin.terminal.LanternaTerminalUI;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Main entry point for the Card Repetition application.
 * Supports both CLI and TUI interfaces.
 * 
 * Usage:
 *   java -jar card-rep.jar          # Default: CLI mode
 *   java -jar card-rep.jar --cli    # Explicit: CLI mode
 *   java -jar card-rep.jar --tui    # TUI mode with vim-style navigation
 */
public class CardRepApp {

    public static void main(String[] args) {
        boolean useTui = Arrays.asList(args).contains("--tui");

        // Plugins (shared between CLI and TUI)
        CardRepository cardRepository = new InMemoryCardRepository();
        DeckRepository deckRepository = new InMemoryDeckRepository();
        CollectionRepository collectionRepository = new InMemoryCollectionRepository();

        // Algorithms (shared between CLI and TUI)
        RandomRepetitionAlgorithm randomAlgorithm = new RandomRepetitionAlgorithm();
        SpacedRepetitionAlgorithm spacedAlgorithm = new SpacedRepetitionAlgorithm();

        // Use Cases (shared between CLI and TUI)
        CreateCardUseCase createCard = new CreateCardUseCase(cardRepository, deckRepository);
        ModifyCardUseCase modifyCard = new ModifyCardUseCase(cardRepository);
        DeleteCardUseCase deleteCard = new DeleteCardUseCase(cardRepository, deckRepository);

        CreateDeckUseCase createDeck = new CreateDeckUseCase(deckRepository, collectionRepository);
        ModifyDeckUseCase modifyDeck = new ModifyDeckUseCase(deckRepository);
        DeleteDeckUseCase deleteDeck = new DeleteDeckUseCase(deckRepository, cardRepository, collectionRepository);
        NextCardUseCase nextCard = new NextCardUseCase(deckRepository);

        CreateCollectionUseCase createCollection = new CreateCollectionUseCase(collectionRepository);
        ModifyCollectionUseCase modifyCollection = new ModifyCollectionUseCase(collectionRepository);
        DeleteCollectionUseCase deleteCollection = new DeleteCollectionUseCase(
                collectionRepository, deckRepository, cardRepository);

        LearnCardUseCase learnCard = new LearnCardUseCase(cardRepository, deckRepository);

        if (useTui) {
            runTui(collectionRepository, deckRepository,
                    createCollection, modifyCollection, deleteCollection,
                    createDeck, modifyDeck, deleteDeck,
                    createCard, modifyCard, deleteCard,
                    nextCard, learnCard,
                    spacedAlgorithm, randomAlgorithm);
        } else {
            runCli(cardRepository, deckRepository, collectionRepository,
                    createCard, modifyCard, deleteCard,
                    createDeck, modifyDeck, deleteDeck, nextCard,
                    createCollection, modifyCollection, deleteCollection,
                    learnCard,
                    randomAlgorithm, spacedAlgorithm);
        }
    }

    /**
     * Run the TUI (Text User Interface) with vim-style navigation.
     */
    private static void runTui(CollectionRepository collectionRepository,
                               DeckRepository deckRepository,
                               CreateCollectionUseCase createCollection,
                               ModifyCollectionUseCase modifyCollection,
                               DeleteCollectionUseCase deleteCollection,
                               CreateDeckUseCase createDeck,
                               ModifyDeckUseCase modifyDeck,
                               DeleteDeckUseCase deleteDeck,
                               CreateCardUseCase createCard,
                               ModifyCardUseCase modifyCard,
                               DeleteCardUseCase deleteCard,
                               NextCardUseCase nextCard,
                               LearnCardUseCase learnCard,
                               SpacedRepetitionAlgorithm spacedAlgorithm,
                               RandomRepetitionAlgorithm randomAlgorithm) {
        TerminalUI terminal = new LanternaTerminalUI();
        CardRepTui tui = new CardRepTui(terminal,
                collectionRepository, deckRepository,
                createCollection, modifyCollection, deleteCollection,
                createDeck, modifyDeck, deleteDeck,
                createCard, modifyCard, deleteCard,
                nextCard, learnCard,
                spacedAlgorithm, randomAlgorithm);
        tui.run();
    }

    /**
     * Run the traditional CLI (Command Line Interface).
     */
    private static void runCli(CardRepository cardRepository,
                               DeckRepository deckRepository,
                               CollectionRepository collectionRepository,
                               CreateCardUseCase createCard,
                               ModifyCardUseCase modifyCard,
                               DeleteCardUseCase deleteCard,
                               CreateDeckUseCase createDeck,
                               ModifyDeckUseCase modifyDeck,
                               DeleteDeckUseCase deleteDeck,
                               NextCardUseCase nextCard,
                               CreateCollectionUseCase createCollection,
                               ModifyCollectionUseCase modifyCollection,
                               DeleteCollectionUseCase deleteCollection,
                               LearnCardUseCase learnCard,
                               RandomRepetitionAlgorithm randomAlgorithm,
                               SpacedRepetitionAlgorithm spacedAlgorithm) {
        // Observer
        DeckStatsLogger statsLogger = new DeckStatsLogger();

        Scanner scanner = new Scanner(System.in);

        // Shared menu helper (DRY: eliminates duplicated selection logic)
        MenuHelper menuHelper = new MenuHelper(scanner, deckRepository, collectionRepository);

        // Build menus
        CardMenu cardMenu = new CardMenu(scanner, createCard, modifyCard, deleteCard,
                deckRepository, menuHelper);
        DeckMenu deckMenu = new DeckMenu(scanner, createDeck, modifyDeck, deleteDeck,
                collectionRepository, randomAlgorithm, spacedAlgorithm, statsLogger, menuHelper);
        CollectionMenu collectionMenu = new CollectionMenu(scanner, createCollection,
                modifyCollection, deleteCollection, collectionRepository, menuHelper);
        LearningSession learningSession = new LearningSession(scanner, nextCard, learnCard, menuHelper);

        MainMenu mainMenu = new MainMenu(scanner, cardMenu, deckMenu, collectionMenu,
                learningSession, collectionRepository);

        System.out.println("===========================================");
        System.out.println("   Card Repetition - Spaced Learning App");
        System.out.println("===========================================");
        System.out.println();

        mainMenu.run();

        scanner.close();
        System.out.println("Goodbye!");
    }
}
