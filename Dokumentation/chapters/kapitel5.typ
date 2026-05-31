= Unit Tests

== 10 Unit Tests

#figure(
  table(
    columns: (auto, 1fr),
    align: (left, left),
    stroke: 0.5pt,
    inset: 8pt,
    table.header([*\#*], [*Unit Test*]),
    [1], [`CardTest\#createCard_withValidContent_shouldSucceed` \ Testet erfolgreiche Karten-Erstellung mit gültigem Front/Back-Content],
    [2], [`CardTest\#createCard_withNullFront_shouldThrow` \ Testet, dass null-Front eine Exception wirft],
    [3], [`DeckTest\#addCard_duplicateId_shouldThrow` \ Testet, dass doppeltes Hinzufügen einer Karte (gleiche ID) eine Exception wirft],
    [4], [`DeckTest\#computeStats_withReviewedCards_shouldAggregateCorrectly` \ Testet korrekte Aggregation von DeckStats nach Reviews],
    [5], [`DeckTest\#observer_shouldBeNotifiedOnCardAdd` \ Testet, dass Observer bei `addCard()` benachrichtigt werden],
    [6], [`CollectionTest\#addChildCollection_withDuplicateName_shouldThrow` \ Testet Business-Regel: eindeutige Namen innerhalb einer Collection],
    [7], [`CardUseCaseTest\#createCard_shouldSaveCardAndAddToDeck` \ Testet Use-Case-Orchestrierung: Karte wird gespeichert UND zum Deck hinzugefügt],
    [8], [`DeckUseCaseTest\#deleteDeck_shouldDeleteAllCardsAndDeck` \ Testet Cascade-Delete: alle Karten und das Deck werden entfernt],
    [9], [`InMemoryCardRepositoryTest\#save_shouldStoreCard` \ Testet, dass die Fake-Implementierung korrekt speichert],
    [10], [`RepetitionAlgorithmTest\#spacedRepetition_shouldPrioritizeUnreviewedCards` \ Testet, dass ungeprüfte Karten bevorzugt werden],
  ),
  caption: [Übersicht der 10 Unit Tests],
)

== ATRIP: Automatic

Alle Tests laufen automatisch via `mvn test` ohne manuelle Intervention. JUnit 5 entdeckt und führt alle Tests aus. JaCoCo generiert Coverage-Reports automatisch. Keine manuellen Schritte, keine UI-Interaktion, keine Datenbank-Setup erforderlich: die In-Memory-Repositories starten leer und brauchen keine Konfiguration.

== ATRIP: Thorough

Positiv-Beispiel: `DeckTest` (13 Tests)

```java
@Test
void addCard_duplicateId_shouldThrow() {
    deck.addCard(card);
    assertThrows(IllegalArgumentException.class, () -> deck.addCard(card));
}

@Test
void removeCard_nonExistentCard_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> deck.removeCard("non-existent-id"));
}

@Test
void getNextCard_emptyDeck_shouldReturnNull() {
    assertNull(deck.getNextCard());
}
```

Analyse: `DeckTest` testet sowohl Happy Paths (addCard, removeCard, computeStats) als auch Edge Cases (doppelte Karte, nicht existente Karte, leeres Deck, null-Parameter). Alle Grenzfälle der Geschäftslogik sind abgedeckt.

Negativ-Beispiel: `InMemoryCardRepositoryTest` (6 Tests)

```java
@Test
void deleteById_shouldRemoveCard() {
    repository.save(card);
    repository.deleteById(card.getId());
    assertFalse(repository.existsById(card.getId()));
}
```

Analyse: Es fehlt ein Test für `deleteById()` mit einer nicht existierenden ID: das Verhalten bei ungültiger ID wird nicht getestet. Ebenso fehlt ein Test für `save()` mit einer bereits existierenden Karte (Update-Verhalten). Die Tests decken nur den Standard-Pfad ab.

== ATRIP: Professional

Positiv-Beispiel: `CardUseCaseTest`

```java
@Test
void createCard_shouldSaveCardAndAddToDeck() {
    when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
    when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

    Card result = createCardUseCase.execute(deck.getId(), front, back);

    assertNotNull(result);
    assertEquals(front, result.getFront());
    verify(cardRepository).save(any(Card.class));
    verify(deckRepository).save(deck);
}
```

Analyse: 

Professionell: sprechende Methodennamen (`action_expectedBehavior`-Muster), klares Arrange-Act-Assert-Muster, gezielte Verifizierung der Interaktionen, eigene `@BeforeEach`-Setup pro Test.

Negativ-Beispiel: `RepetitionAlgorithmTest`

```java
@Test
void randomRepetition_shouldReturnACard() {
    Card result = randomAlgorithm.selectNextCard(cards);
    assertNotNull(result);
    assertTrue(cards.contains(result));
}
```

Analyse: Weniger professionell: der Testname `shouldReturnACard` ist vage: er beschreibt nicht die Vorbedingung oder das erwartete Ergebnis präzise genug. Besser wäre: `selectNextCard_withMultipleCards_shouldReturnCardFromList`. Außerdem testet er nur dass _irgendeine_ Karte zurückgegeben wird, nicht ob die Verteilung korrekt ist.

== Code Coverage

Coverage wird via JaCoCo gemessen (`mvn test jacoco:report`). Die Domain- und Application-Schichten haben die höchste Coverage, da sie die Kerngeschäftslogik enthalten und am gründlichsten getestet sind. Die Adapter-Schicht (CLI) hat niedrigere Coverage, da sie primär User-I/O verarbeitet, was manuell getestet wird. Insgesamt sind 61 Tests vorhanden, die die wichtigsten Geschäftsregeln und Edge Cases abdecken.

== Fakes und Mocks

=== Mock: CardRepository in CardUseCaseTest

#figure(
  image("../assets/diagrams/Mock-CardRepository-in-CardUseCaseTest.png"),
  caption: [Mockito-Mocks für isolierte Use-Case-Tests],
)

Analyse: Mockito-Mocks werden verwendet, um die Use Cases isoliert von der Persistenz zu testen. `when(...).thenReturn(...)` definiert erwartetes Verhalten, `verify(...)` prüft, dass die korrekten Repository-Methoden aufgerufen wurden. Vorteil: Der Test ist schnell, deterministisch, und unabhängig von der Implementierung.

```java
@Mock private CardRepository cardRepository;
@Mock private DeckRepository deckRepository;

@Test
void deleteDeck_shouldDeleteAllCardsAndDeck() {
    when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
    deleteDeckUseCase.execute(deck.getId(), collectionId);
    verify(cardRepository).deleteById(card1.getId());  // Interaktion verifizieren
}
```

=== Fake: InMemoryCardRepository in InMemoryCardRepositoryTest

#figure(
  image("../assets/diagrams/Fake-InMemoryCardRepository-in-InMemoryCardRepositoryTest.png"),
  caption: [InMemoryCardRepository als Fake-Implementierung],
)

Analyse: `InMemoryCardRepository` ist ein Fake: eine echte, funktionierende Implementierung (HashMap-basiert) des `CardRepository`-Interfaces. Im Gegensatz zu einem Mock hat es echtes Verhalten und echten Zustand. Es wird in Tests verwendet, um die Korrektheit der Implementierung selbst zu verifizieren, und dient im Produktionscode als leichtgewichtige Persistenz.

```java
private InMemoryCardRepository repository;

@BeforeEach
void setUp() {
    repository = new InMemoryCardRepository();  // Echte Implementierung, kein Mock
}

@Test
void save_shouldStoreCard() {
    repository.save(card);
    assertTrue(repository.existsById(card.getId()));
}
```
