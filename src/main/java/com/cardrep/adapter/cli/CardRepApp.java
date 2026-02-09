package com.cardrep.adapter.cli;

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
import com.cardrep.infrastructure.algorithm.RandomRepetitionAlgorithm;
import com.cardrep.infrastructure.algorithm.SpacedRepetitionAlgorithm;
import com.cardrep.infrastructure.observer.DeckStatsLogger;
import com.cardrep.infrastructure.persistence.InMemoryCardRepository;
import com.cardrep.infrastructure.persistence.InMemoryCollectionRepository;
import com.cardrep.infrastructure.persistence.InMemoryDeckRepository;

import java.util.Scanner;

/**
 * Main entry point for the Card Repetition CLI application.
 * Wires up all dependencies and delegates to the main menu.
 */
public class CardRepApp {

    public static void main(String[] args) {
        // Infrastructure
        CardRepository cardRepository = new InMemoryCardRepository();
        DeckRepository deckRepository = new InMemoryDeckRepository();
        CollectionRepository collectionRepository = new InMemoryCollectionRepository();

        // Algorithms
        RandomRepetitionAlgorithm randomAlgorithm = new RandomRepetitionAlgorithm();
        SpacedRepetitionAlgorithm spacedAlgorithm = new SpacedRepetitionAlgorithm();

        // Observer
        DeckStatsLogger statsLogger = new DeckStatsLogger();

        // Use Cases
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

        Scanner scanner = new Scanner(System.in);

        // Build menus
        CardMenu cardMenu = new CardMenu(scanner, createCard, modifyCard, deleteCard, deckRepository);
        DeckMenu deckMenu = new DeckMenu(scanner, createDeck, modifyDeck, deleteDeck,
                collectionRepository, randomAlgorithm, spacedAlgorithm, statsLogger);
        CollectionMenu collectionMenu = new CollectionMenu(scanner, createCollection,
                modifyCollection, deleteCollection, collectionRepository);
        LearningSession learningSession = new LearningSession(scanner, nextCard, learnCard, deckRepository);

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
