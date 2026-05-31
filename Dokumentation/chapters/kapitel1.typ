= Einführung

== Übersicht über die Applikation

Card Repetition ist eine terminalbasierte Lernapplikation für Spaced Repetition (ähnlich Anki), implementiert in Java 25 mit Maven. Benutzer organisieren Lernmaterial in Collections (hierarchische Ordner), die Decks enthalten, welche wiederum Cards (Vorder-/Rückseite-Karteikarten) beinhalten. Während einer Learning Session werden Karten basierend auf einem konfigurierbaren Repetition Algorithm (Strategy Pattern) präsentiert. Der Benutzer bewertet die wahrgenommene Difficulty, und das System verfolgt CardStats und DeckStats, wobei Observer (Observer Pattern) über Statistikänderungen benachrichtigt werden.

Die Applikation löst das Problem des effizienten Lernens durch Wiederholung: Schwierige Karten werden häufiger präsentiert als leichte, was den Lerneffekt maximiert.

== Wie startet man die Applikation?

Voraussetzungen:
- Java 25 (JDK)
- Maven 3.x

Schritte:

```bash
# Repository klonen
git clone git@github.com:GilbertBckr/card-rep.git
cd card-rep

# Projekt kompilieren
mvn clean compile

# Applikation starten
mvn exec:java -Dexec.mainClass="com.cardrep.adapter.cli.CardRepApp"
```

== Wie testet man die Applikation?

```bash
# Alle Unit Tests ausführen
mvn test

# Test Coverage Report generieren
mvn test jacoco:report
# Report öffnen: target/site/jacoco/index.html
```

== Technology Stack

#table(
  columns: (1fr, 1fr),
  align: (left, left),
  table.header([*Komponente*], [*Technologie*]),
  [Sprache], [Java 25],
  [Build Tool], [Maven],
  [Testing], [JUnit 5.10.2],
  [Mocking], [Mockito 5.18.0],
  [Code Coverage], [JaCoCo 0.8.14],
  [Persistenz], [In-Memory (HashMap)],
)

== Kommentar-Richtlinie

Dieses Projekt folgt dem Clean-Code-Prinzip für Kommentare: Code soll selbsterklärend sein. Kommentare werden ausschließlich für nicht-offensichtliche Design-Entscheidungen verwendet (z.B. Geschäftsregeln, Invarianten, bewusste Einschränkungen, Kaskadenverhalten). Entwurfsmuster-Annotationen werden nicht kommentiert, da sie aus der Code-Struktur ersichtlich sind. Erklärende Kommentare, die lediglich beschreiben was der Code tut, werden vermieden: stattdessen drücken aussagekräftige Methoden- und Variablennamen die Intention aus.
