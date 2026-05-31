= SOLID

== Analyse Single-Responsibility-Principle (SRP)

=== Positiv-Beispiel: CreateCardUseCase

#figure(
  image("../assets/diagrams/Positiv-Beispiel-CreateCardUseCase.png"),
  caption: [CreateCardUseCase: genau eine Verantwortlichkeit],
)

*Aufgabe:* `CreateCardUseCase` (`src/main/java/com/cardrep/application/card/CreateCardUseCase.java`) hat genau eine Verantwortlichkeit: das Erstellen einer neuen Karte und deren Hinzufügen zu einem Deck. Es validiert die Deck-Existenz, erstellt die Karte, speichert sie und fügt sie dem Deck hinzu. Wenn sich die Karten-Erstellungslogik ändert, muss nur diese Klasse angepasst werden.

=== Negativ-Beispiel: DeckMenu

#figure(
  image("../assets/diagrams/Negativ-Beispiel-DeckMenu.png"),
  caption: [DeckMenu: mehrere Verantwortlichkeiten in einer Klasse],
)

*Aufgaben:* `DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java`) hat mehrere Verantwortlichkeiten:
1. CRUD-Operationen für Decks
2. Algorithmus-Auswahl
3. Statistik-Anzeige
4. User-Input-Parsing

Es hat vier Gründe sich zu ändern: (1) Deck-Operationen ändern sich, (2) Algorithmus-Auswahl ändert sich, (3) Statistik-Anzeige ändert sich, (4) Input-Handling ändert sich.

*Möglicher Lösungsweg:*

#figure(
  image("../assets/diagrams/Negativ-Beispiel-DeckMenu-Möglicher-Lösungsweg.png"),
  caption: [SRP-konformer Lösungsweg: Aufteilen in DeckMenuActions und AlgorithmSelector],
)

== Analyse Open-Closed-Principle (OCP)

=== Positiv-Beispiel: RepetitionAlgorithm

#figure(
  image("../assets/diagrams/Positiv-Beispiel-RepetitionAlgorithm.png"),
  caption: [RepetitionAlgorithm: offen für Erweiterung durch neue Implementierungen],
)

*Analyse:* Das `RepetitionAlgorithm`-Interface (`src/main/java/com/cardrep/domain/service/RepetitionAlgorithm.java`) ist offen für Erweiterung: Neue Algorithmen (z.B. `LeitnerAlgorithm`) können durch Implementierung des Interfaces hinzugefügt werden, ohne bestehenden Code zu modifizieren. Die existierenden Klassen `RandomRepetitionAlgorithm` und `SpacedRepetitionAlgorithm` bleiben unverändert. Das ist hier sinnvoll, weil verschiedene Lernstrategien ein natürlicher Erweiterungspunkt sind.

=== Negativ-Beispiel: DeckMenu.selectAlgorithm()

#figure(
  image("../assets/diagrams/Negativ-Beispiel-DeckMenu.selectAlgorithm().png"),
  caption: [selectAlgorithm(): hardcodiertes Switch-Statement verletzt OCP],
)

*Analyse:* Die Methode `selectAlgorithm()` in `DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:183-198`) verwendet ein hardcodiertes Switch-Statement:

```java
return switch (choice) {
    case "1" -> spacedAlgorithm;
    case "2" -> randomAlgorithm;
    default -> spacedAlgorithm;
};
```

Das Hinzufügen eines neuen Algorithmus erfordert eine Modifikation dieser Methode: OCP verletzt.

*Möglicher Lösungsweg:*

#figure(
  image("../assets/diagrams/Negativ-Beispiel-DeckMenu.selectAlgorithm()-Möglicher-Lösungsweg.png"),
  caption: [OCP-konformer Lösungsweg: dynamisches Menü über List\<RepetitionAlgorithm\>],
)

Lösung: Eine `List<RepetitionAlgorithm>` injizieren und das Menü dynamisch aufbauen. Neue Algorithmen werden einfach zur Liste hinzugefügt.

== Analyse Dependency-Inversion-Principle (DIP)

=== Positiv-Beispiel: DeleteDeckUseCase

#figure(
  image("../assets/diagrams/Positiv-Beispiel-DeleteDeckUseCase.png"),
  caption: [DeleteDeckUseCase: Abhängigkeit ausschließlich von Abstraktionen],
)

Begründung: `DeleteDeckUseCase` (`src/main/java/com/cardrep/application/deck/DeleteDeckUseCase.java`) hängt ausschließlich von Abstraktionen ab (Interfaces definiert in der Domain-Schicht). Die konkreten Implementierungen (`InMemoryDeckRepository` etc.) werden am Composition Root (`CardRepApp.java`) injiziert. Das High-Level-Modul (Use Case) ist vom Low-Level-Modul (In-Memory Storage) entkoppelt.

=== Negativ-Beispiel: DeckMenu

#figure(
  image("../assets/diagrams/(DIP)-Negativ-Beispiel-DeckMenu.png"),
  caption: [DeckMenu: direkte Abhängigkeit von konkreten Klassen],
)

Begründung: `DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:25-46`) hängt direkt von konkreten Klassen ab statt von Abstraktionen:

```java
// Verletzung: Konstruktor-Parameter sind konkrete Typen, keine Abstraktionen
private final RandomRepetitionAlgorithm randomAlgorithm;
private final SpacedRepetitionAlgorithm spacedAlgorithm;
```

Eine DIP-konforme Lösung wäre: `private final List<RepetitionAlgorithm> algorithms;`: dann hängt `DeckMenu` nur von der Abstraktion `RepetitionAlgorithm` ab.
