= Weitere Prinzipien

== Analyse GRASP: Geringe Kopplung

=== Positiv-Beispiel: CardStats

#figure(
  image("../assets/diagrams/Positiv-Beispiel-CardStats.png"),
  caption: [CardStats: geringe Kopplung durch minimale Abhängigkeiten],
)

Aufgabe: `CardStats` (`src/main/java/com/cardrep/domain/model/CardStats.java`) verwaltet die Review-Historie einer Karte (Zeitpunkte, Schwierigkeitsgrade, Statistiken).

Begründung geringe Kopplung: `CardStats` hat nur Abhängigkeiten zu seinen eigenen inneren Typen (`ReviewEntry`) und dem einfachen Enum `Difficulty`. Es hat keine Abhängigkeiten zu Repositories, Services, oder anderen Schichten. Die `withNewReview()`-Methode gibt eine neue Instanz zurück (Immutability), was die Kopplung weiter reduziert: keine Seiteneffekte durch geteilten Zustand.

=== Negativ-Beispiel: DeckMenu

#figure(
  image("../assets/diagrams/Analyse GRASP-Geringe Kopplung-Negativ-Beispiel-DeckMenu.png"),
  caption: [DeckMenu: hohe Kopplung durch 9 Abhängigkeiten],
)

Aufgabe: `DeckMenu` verwaltet das CLI-Menü für Deck-Operationen.

Begründung hohe Kopplung: `DeckMenu` hat 9 Abhängigkeiten (8 injizierte + Scanner). Es ist an 3 Use Cases, 2 konkrete Algorithmen, 1 Repository, 1 Observer und 1 Helper gekoppelt. Das Hinzufügen oder Ändern eines Algorithmus erfordert Änderungen in `DeckMenu`. Auflösung: Die Algorithmus-Auswahl in eine eigene `AlgorithmSelector`-Klasse extrahieren und die konkreten Typen durch eine `List<RepetitionAlgorithm>` ersetzen würde die Kopplung von 9 auf 7 Abhängigkeiten reduzieren und die konkrete Plugin-Abhängigkeit entfernen.

== Analyse GRASP: Hohe Kohäsion

=== Positiv-Beispiel: CardContent

#figure(
  image("../assets/diagrams/Positiv-Beispiel-CardContent.png"),
  caption: [CardContent: maximale Kohäsion],
)

Begründung: `CardContent` (`src/main/java/com/cardrep/domain/model/CardContent.java`) ist maximal kohäsiv: alle Felder (`text`, `imagePath`) und alle Methoden (`getText()`, `getImagePath()`, `hasImage()`) dienen ausschließlich dem Konzept "Karteninhalt". Es gibt keine Methode, die nichts mit dem Textinhalt zu tun hat. Die Klasse ist immutable (alle Felder `final`), was die Kohäsion weiter stärkt: jede Instanz repräsentiert genau einen konsistenten Zustand.

=== Negativ-Beispiel: DeckMenu

#figure(
  image("../assets/diagrams/Analyse GRASP-Hohe Kohäsion-Negativ-Beispiel-DeckMenu.png"),
  caption: [DeckMenu: niedrige Kohäsion durch vier funktionale Bereiche],
)

Begründung: `DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java`) hat niedrige Kohäsion: seine Methoden bedienen vier verschiedene funktionale Bereiche: (1) Deck-CRUD (`createDeck`, `modifyDeck`, `deleteDeck`), (2) Algorithmus-Auswahl (`selectAlgorithm`), (3) Statistik-Anzeige (`viewDeckStats`), (4) User-Input-Parsing. Nicht alle Felder werden von allen Methoden genutzt: z.B. `randomAlgorithm` und `spacedAlgorithm` werden nur von `selectAlgorithm()` verwendet, `statsObserver` nur von `createDeck()`. Die geringe Überlappung der Feld-Nutzung zwischen Methoden ist ein Indikator für niedrige Kohäsion. Auflösung: `selectAlgorithm()` und die zugehörigen Felder in eine eigene `AlgorithmSelector`-Klasse extrahieren würde die Kohäsion der verbleibenden `DeckMenu`-Klasse erhöhen.

== Don't Repeat Yourself (DRY)

Commit: `aafecbc` (Refactoring: Extract MenuHelper to eliminate duplicated selection logic)

Vorher (CardMenu.java): duplizierte `selectDeck()`-Methode:

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
    try {
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= decks.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return decks.get(index).getId();
    } catch (NumberFormatException e) {
        System.out.println("Invalid input.");
        return null;
    }
}
```

Die gleiche Logik war in `CardMenu` und `LearningSession` dupliziert. Ebenso war `selectCollection()` in `DeckMenu` und `CollectionMenu` dupliziert.

Nachher (MenuHelper.java): generische Methode:

```java
public <T> String selectFromList(List<T> items, String header,
                                 Function<T, String> displayFunc,
                                 Function<T, String> idFunc) {
    if (items.isEmpty()) {
        System.out.println("No items available.");
        return null;
    }
    System.out.println("\n" + header + ":");
    for (int i = 0; i < items.size(); i++) {
        System.out.println("  " + (i + 1) + ". " + displayFunc.apply(items.get(i)));
    }
    System.out.print("Select (number): ");
    try {
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return idFunc.apply(items.get(index));
    } catch (NumberFormatException e) {
        System.out.println("Invalid input.");
        return null;
    }
}
```

Auswirkung: 4 Klassen delegieren jetzt an `MenuHelper`. Die generische `selectFromList()`-Methode kann für jede zukünftige Listen-Auswahl wiederverwendet werden. Netto-Änderung: +190 Zeilen (neue Datei), -208 Zeilen (entfernter Code) = 18 Zeilen weniger bei besserer Struktur.
