= Refactoring

== Code Smells

=== Code Smell 1: Duplicated Code (selectDeck in CardMenu und LearningSession)

Code-Beispiel (CardMenu.java, vor Refactoring):

```java
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
    // ... parsing logic
}
```

Nahezu identischer Code existierte in `LearningSession.java`. Ebenso war `selectCollection()` in `DeckMenu` und `CollectionMenu` dupliziert.

Lösungsweg: Extract Class: alle Selektions-Methoden wurden in eine neue Klasse `MenuHelper` zentralisiert. Eine generische `selectFromList()`-Methode nutzt Java Generics und `Function`-Parameter, um für jede Entity-Auswahl wiederverwendbar zu sein.

=== Code Smell 2: Long Method (Deck.computeStats)

Code-Beispiel (Deck.java, vor Refactoring):

```java
public DeckStats computeStats() {
    int total = cards.size();
    int reviewed = 0;
    int easy = 0, medium = 0, hard = 0, failed = 0;
    for (Card card : cards) {
        if (card.getStats().hasBeenReviewed()) {
            reviewed++;
            Difficulty lastDifficulty = card.getStats().getLastDifficulty();
            if (lastDifficulty == Difficulty.EASY) easy++;
            else if (lastDifficulty == Difficulty.MEDIUM) medium++;
            else if (lastDifficulty == Difficulty.HARD) hard++;
            else if (lastDifficulty == Difficulty.AGAIN) failed++;
        }
    }
    return new DeckStats(total, reviewed, easy, medium, hard, failed);
}
```

Die Methode mischt Filterlogik (welche Karten wurden reviewed?) mit Zähllogik (Kategorisierung nach Difficulty) in einer einzigen Schleife mit verschachtelten if-else-Ketten.

Lösungsweg: Extract Method: Aufteilen in `getReviewedCards()` (filtert reviewed Karten) und `countByLastDifficulty(cards, difficulty)` (zählt Karten nach Schwierigkeit). Jede Methode hat eine einzige Verantwortlichkeit, die deklarative Stream-basierte Lösung ersetzt die imperative Schleife.

== 2 Refactorings

=== Refactoring 1: Extract Class: MenuHelper (DRY)

Commit: `aafecbc`

Problem: `selectDeck()` war in `CardMenu` und `LearningSession` dupliziert. `selectCollection()` war in `DeckMenu` und `CollectionMenu` dupliziert. Ca. 120 Zeilen duplizierter Code über 4 Klassen.

UML Class Diagram (Vorher: duplizierte Methoden):

#figure(
  image("../assets/diagrams/Refactoring-DRY-selectDeck()-vorher.png"),
  caption: [Vorher: duplizierte Selektions-Methoden in 4 Klassen],
)

UML Class Diagram (Nachher: extrahierte MenuHelper):

#figure(
  image("../assets/diagrams/Refactoring-DRY-selectDeck()-nachher.png"),
  caption: [Nachher: zentralisierte MenuHelper-Klasse],
)

Begründung: DRY-Prinzip verletzt. Die generische `selectFromList()`-Methode nutzt Java Generics und `Function`-Parameter, um für jede Entity-Auswahl wiederverwendbar zu sein.

=== Refactoring 2: Extract Method: Deck.computeStats()

Commit: `aafecbc`

Problem: `computeStats()` war eine monolithische Methode mit Schleife und verschachtelten if-else-Ketten. Sie mischte Filterlogik mit Zähllogik.

UML Class Diagram (Vorher: monolithische Methode):

#figure(
  image("../assets/diagrams/Refactoring-Extract-Method-computeStats()-vorher.png"),
  caption: [Vorher: monolithische computeStats()-Methode],
)

Vorher (Deck.java):

```java
public DeckStats computeStats() {
    int total = cards.size();
    int reviewed = 0;
    int easy = 0, medium = 0, hard = 0, failed = 0;

    for (Card card : cards) {
        if (card.getStats().hasBeenReviewed()) {
            reviewed++;
            Difficulty lastDifficulty = card.getStats().getLastDifficulty();
            if (lastDifficulty == Difficulty.EASY) easy++;
            else if (lastDifficulty == Difficulty.MEDIUM) medium++;
            else if (lastDifficulty == Difficulty.HARD) hard++;
            else if (lastDifficulty == Difficulty.AGAIN) failed++;
        }
    }
    return new DeckStats(total, reviewed, easy, medium, hard, failed);
}
```

UML Class Diagram (Nachher: extrahierte Methoden):

#figure(
  image("../assets/diagrams/Refactoring-Extract-Method-computeStats()-nachher.png"),
  caption: [Nachher: fokussierte Helper-Methoden],
)

Nachher (Deck.java):

```java
public DeckStats computeStats() {
    int total = cards.size();
    List<Card> reviewedCards = getReviewedCards();
    int reviewed = reviewedCards.size();
    return new DeckStats(total, reviewed,
            countByLastDifficulty(reviewedCards, Difficulty.EASY),
            countByLastDifficulty(reviewedCards, Difficulty.MEDIUM),
            countByLastDifficulty(reviewedCards, Difficulty.HARD),
            countByLastDifficulty(reviewedCards, Difficulty.AGAIN));
}

private List<Card> getReviewedCards() {
    return cards.stream()
            .filter(card -> card.getStats().hasBeenReviewed())
            .toList();
}

private int countByLastDifficulty(List<Card> reviewedCards, Difficulty difficulty) {
    return (int) reviewedCards.stream()
            .filter(card -> card.getStats().getLastDifficulty() == difficulty)
            .count();
}
```

Begründung: Jede Methode hat jetzt eine einzige Verantwortlichkeit: `getReviewedCards()` filtert, `countByLastDifficulty()` zählt. Die deklarative Stream-basierte Lösung ersetzt die imperative Schleife mit mutablen Zählern.
