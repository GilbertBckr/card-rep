package com.cardrep.application;

import com.cardrep.application.card.CreateCardUseCase;
import com.cardrep.application.card.DeleteCardUseCase;
import com.cardrep.application.card.ModifyCardUseCase;
import com.cardrep.domain.model.Card;
import com.cardrep.domain.model.CardContent;
import com.cardrep.domain.model.Deck;
import com.cardrep.domain.repository.CardRepository;
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
 * Tests for Card use cases using Mockito mocks for repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class CardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DeckRepository deckRepository;

    private CreateCardUseCase createCardUseCase;
    private ModifyCardUseCase modifyCardUseCase;
    private DeleteCardUseCase deleteCardUseCase;

    private RepetitionAlgorithm dummyAlgorithm;

    @BeforeEach
    void setUp() {
        createCardUseCase = new CreateCardUseCase(cardRepository, deckRepository);
        modifyCardUseCase = new ModifyCardUseCase(cardRepository);
        deleteCardUseCase = new DeleteCardUseCase(cardRepository, deckRepository);

        dummyAlgorithm = new RepetitionAlgorithm() {
            @Override
            public Card selectNextCard(List<Card> cards) {
                return cards.isEmpty() ? null : cards.get(0);
            }

            @Override
            public String getName() {
                return "Dummy";
            }
        };
    }

    @Test
    void createCard_shouldSaveCardAndAddToDeck() {
        Deck deck = Deck.create("Test Deck", dummyAlgorithm);
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deckRepository.save(any(Deck.class))).thenAnswer(inv -> inv.getArgument(0));

        CardContent front = new CardContent("What is 2+2?");
        CardContent back = new CardContent("4");
        Card result = createCardUseCase.execute(deck.getId(), front, back);

        assertNotNull(result);
        assertEquals("What is 2+2?", result.getFront().getText());
        assertEquals("4", result.getBack().getText());
        verify(cardRepository).save(any(Card.class));
        verify(deckRepository).save(deck);
        assertEquals(1, deck.getCards().size());
    }

    @Test
    void createCard_withNonExistentDeck_shouldThrow() {
        when(deckRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> createCardUseCase.execute("bad-id",
                        new CardContent("Q"), new CardContent("A")));
    }

    @Test
    void modifyCard_shouldUpdateContentPreserveStats() {
        Card card = Card.create(new CardContent("Old Q"), new CardContent("Old A"));
        card.recordReview(com.cardrep.domain.model.Difficulty.EASY);
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        Card result = modifyCardUseCase.execute(card.getId(),
                new CardContent("New Q"), new CardContent("New A"));

        assertEquals("New Q", result.getFront().getText());
        assertEquals("New A", result.getBack().getText());
        assertEquals(1, result.getStats().getTotalReviews());
    }

    @Test
    void deleteCard_shouldRemoveFromDeckAndRepository() {
        Deck deck = Deck.create("Deck", dummyAlgorithm);
        Card card = Card.create(new CardContent("Q"), new CardContent("A"));
        deck.addCard(card);

        when(cardRepository.existsById(card.getId())).thenReturn(true);
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(inv -> inv.getArgument(0));

        deleteCardUseCase.execute(card.getId(), deck.getId());

        verify(cardRepository).deleteById(card.getId());
        assertTrue(deck.getCards().isEmpty());
    }

    @Test
    void deleteCard_nonExistentCard_shouldThrow() {
        when(cardRepository.existsById("no-card")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> deleteCardUseCase.execute("no-card", "some-deck"));
    }
}
