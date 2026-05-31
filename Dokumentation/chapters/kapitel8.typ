= Entwurfsmuster

== Entwurfsmuster: Strategy Pattern: RepetitionAlgorithm

Das Strategy Pattern erlaubt es, den Scheduling-Algorithmus zur Laufzeit auszuwählen und zu wechseln. Jedes Deck kann einen anderen Algorithmus verwenden, ohne dass die `Deck`-Klasse selbst geändert werden muss.

Sinnvoller Einsatz: Verschiedene Benutzer bevorzugen verschiedene Lernstrategien. Manche wollen zufällige Karten (zum Aufwärmen), andere wollen priorisierte Wiederholung (zum gezielten Lernen). Das Strategy Pattern ermöglicht diesen Wechsel ohne Code-Änderung.

Participants:
- Strategy (Interface): `RepetitionAlgorithm` (`src/main/java/com/cardrep/domain/service/RepetitionAlgorithm.java`)
- Concrete Strategy A: `RandomRepetitionAlgorithm` (`src/main/java/com/cardrep/plugin/algorithm/RandomRepetitionAlgorithm.java`)
- Concrete Strategy B: `SpacedRepetitionAlgorithm` (`src/main/java/com/cardrep/plugin/algorithm/SpacedRepetitionAlgorithm.java`)
- Context: `Deck` (`src/main/java/com/cardrep/domain/model/Deck.java`)

UML Class Diagram (Vorher: ohne Strategy Pattern):

#figure(
  image("../assets/diagrams/UML Class Diagram (Vorher ohne Strategy-Pattern).png"),
  caption: [Vorher: hardcodierte Logik in getNextCard()],
)

UML Class Diagram (Nachher: mit Strategy Pattern):

#figure(
  image("../assets/diagrams/UML Class Diagram (Nachher mit Strategy Pattern).png"),
  caption: [Nachher: Strategy Pattern mit austauschbaren Algorithmen],
)

Funktionsweise:

1. Bei der Deck-Erstellung wird ein `RepetitionAlgorithm` übergeben
2. `Deck.getNextCard()` delegiert an `repetitionAlgorithm.selectNextCard(cards)`
3. Der Algorithmus kann zur Laufzeit über `Deck.setRepetitionAlgorithm()` gewechselt werden
4. `RandomRepetitionAlgorithm` wählt eine zufällige Karte
5. `SpacedRepetitionAlgorithm` wählt die Karte mit der niedrigsten Priorität (ungeprüfte Karten zuerst, dann AGAIN > HARD > MEDIUM > EASY)

== Entwurfsmuster: Observer Pattern: DeckStatsObserver

Das Observer Pattern benachrichtigt interessierte Parteien, wenn sich Deck-Statistiken ändern (z.B. wenn eine Karte hinzugefügt, entfernt oder reviewed wird). Dies entkoppelt die Benachrichtigungslogik vom Domänenmodell.

Sinnvoller Einsatz: Das System muss bei Statistik-Änderungen reagieren (z.B. Logging, UI-Update, Persistenz), ohne dass das Deck alle Empfänger kennen muss. Neue Observer können hinzugefügt werden, ohne die Deck-Klasse zu ändern (OCP).

Participants:
- Subject: `Deck` (`src/main/java/com/cardrep/domain/model/Deck.java`)
- Observer (Interface): `DeckStatsObserver` (`src/main/java/com/cardrep/domain/model/DeckStatsObserver.java`)
- Concrete Observer: `DeckStatsLogger` (`src/main/java/com/cardrep/plugin/observer/DeckStatsLogger.java`)

UML Class Diagram (Vorher: ohne Observer Pattern):

#figure(
  image("../assets/diagrams/UML Class Diagram (Vorher ohne Observer Pattern).png"),
  caption: [Vorher: direkte Kopplung an DeckStatsLogger],
)

UML Class Diagram (Nachher: mit Observer Pattern):

#figure(
  image("../assets/diagrams/UML Class Diagram (Nachher mit Observer Pattern).png"),
  caption: [Nachher: Observer Pattern mit entkoppelter Benachrichtigung],
)

Funktionsweise:

1. `DeckStatsLogger` wird als Observer registriert via `deck.addObserver(statsLogger)`
2. Bei `Deck.addCard()` oder `Deck.removeCard()` wird `notifyObservers()` ausgelöst
3. `notifyObservers()` berechnet aktuelle `DeckStats` und ruft `onDeckStatsChanged(this, stats)` auf jedem registrierten Observer auf
4. `LearnCardUseCase` ruft ebenfalls `deck.notifyObservers()` nach einem Review auf
5. `DeckStatsLogger` loggt die aktualisierten Statistiken auf der Konsole
6. Weitere Observer (z.B. Persistenz-Observer, UI-Updater) können ohne Änderung der Deck-Klasse hinzugefügt werden
