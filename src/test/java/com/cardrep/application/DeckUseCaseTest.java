package com.cardrep.application;

import com.cardrep.application.deck.CreateDeckUseCase;
import com.cardrep.application.deck.DeleteDeckUseCase;
import com.cardrep.application.deck.NextCardUseCase;
import com.cardrep.application.learning.LearnCardUseCase;
import com.cardrep.domain.model.*;
import com.cardrep.domain.repository.CardRepository;
import com.cardrep.domain.repository.CollectionRepository;
import com.cardrep.domain.repository.DeckRepository;
import com.cardrep.domain.service.RepetitionAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for Deck and Learning use cases using Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class DeckUseCaseTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private RepetitionAlgorithm mockAlgorithm;

    private CreateDeckUseCase createDeckUseCase;
    private DeleteDeckUseCase deleteDeckUseCase;
    private NextCardUseCase nextCardUseCase;
    private LearnCardUseCase learnCardUseCase;

    @BeforeEach
    void setUp() {
        createDeckUseCase = new CreateDeckUseCase(deckRepository, collectionRepository);
        deleteDeckUseCase = new DeleteDeckUseCase(deckRepository, cardRepository, collectionRepository);
        nextCardUseCase = new NextCardUseCase(deckRepository);
        learnCardUseCase = new LearnCardUseCase(cardRepository, deckRepository);
    }

    @Test
    void createDeck_shouldSaveDeckAndAddToCollection() {
        Collection collection = Collection.create("Test Collection");
        when(collectionRepository.findById(collection.getId())).thenReturn(Optional.of(collection));
        when(deckRepository.save(any(Deck.class))).thenAnswer(inv -> inv.getArgument(0));
        when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));

        Deck result = createDeckUseCase.execute(collection.getId(), "German Vocab", mockAlgorithm);

        assertNotNull(result);
        assertEquals("German Vocab", result.getName());
        verify(deckRepository).save(any(Deck.class));
        assertEquals(1, collection.getChildDecks().size());
    }

    @Test
    void createDeck_withDuplicateName_shouldThrow() {
        Collection collection = Collection.create("Test");
        Deck existing = Deck.create("Duplicate", mockAlgorithm);
        collection.addDeck(existing);
        when(collectionRepository.findById(collection.getId())).thenReturn(Optional.of(collection));

        assertThrows(IllegalArgumentException.class,
                () -> createDeckUseCase.execute(collection.getId(), "Duplicate", mockAlgorithm));
    }

    @Test
    void deleteDeck_shouldDeleteAllCardsAndDeck() {
        Deck deck = Deck.create("Deck", mockAlgorithm);
        Card card1 = Card.create(new CardContent("Q1"), new CardContent("A1"));
        Card card2 = Card.create(new CardContent("Q2"), new CardContent("A2"));
        deck.addCard(card1);
        deck.addCard(card2);

        Collection collection = Collection.create("Col");
        collection.addDeck(deck);

        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(collectionRepository.findById(collection.getId())).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenAnswer(inv -> inv.getArgument(0));

        deleteDeckUseCase.execute(deck.getId(), collection.getId());

        verify(cardRepository).deleteById(card1.getId());
        verify(cardRepository).deleteById(card2.getId());
        verify(deckRepository).deleteById(deck.getId());
        assertTrue(collection.getChildDecks().isEmpty());
    }

    @Test
    void nextCard_shouldReturnCardFromAlgorithm() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        when(mockAlgorithm.selectNextCard(any())).thenReturn(card);

        Deck deck = Deck.create("Deck", mockAlgorithm);
        deck.addCard(card);
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));

        Card result = nextCardUseCase.execute(deck.getId());

        assertEquals(card, result);
    }

    @Test
    void nextCard_emptyDeck_shouldThrow() {
        Deck deck = Deck.create("Empty Deck", mockAlgorithm);
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));

        assertThrows(IllegalStateException.class,
                () -> nextCardUseCase.execute(deck.getId()));
    }

    @Test
    void learnCard_shouldRecordReviewAndNotifyObservers() {
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        Deck deck = Deck.create("Deck", mockAlgorithm);
        deck.addCard(card);

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));

        // Add observer to verify notification
        final boolean[] notified = {false};
        deck.addObserver((d, stats) -> notified[0] = true);

        Card result = learnCardUseCase.execute(card.getId(), deck.getId(), Difficulty.MEDIUM);

        assertEquals(1, result.getStats().getTotalReviews());
        assertEquals(Difficulty.MEDIUM, result.getStats().getLastDifficulty());
        assertTrue(notified[0]);
    }
}
