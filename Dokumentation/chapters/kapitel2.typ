= Clean Architecture

== Was ist Clean Architecture?

Clean Architecture ist ein Architekturmuster, das Software in konzentrische Schichten unterteilt. Die zentrale Regel ist die Dependency Rule: Abhängigkeiten dürfen nur von außen nach innen zeigen. Die inneren Schichten (Domain, Application) kennen die äußeren Schichten (Plugin, Adapter) nicht. Dies ermöglicht austauschbare Infrastruktur (z.B. Datenbank wechseln), unabhängige Testbarkeit der Geschäftslogik, und klare Trennung der Verantwortlichkeiten. Die Domain-Schicht enthält Entitäten und Geschäftsregeln, die Application-Schicht orchestriert Use Cases, die Plugin-Schicht liefert Implementierungen, und die Adapter-Schicht verbindet mit der Außenwelt.

== Schichtendiagramm

#figure(
  image("../assets/diagrams/Schichtendiagramm.png"),
  caption: [Schichtendiagramm der Clean Architecture],
)

== Analyse der Dependency Rule

=== Positiv-Beispiel: CreateCardUseCase

`CreateCardUseCase` (`src/main/java/com/cardrep/application/card/CreateCardUseCase.java`) hängt ausschließlich von Domain-Interfaces ab (`CardRepository`, `DeckRepository`). Es hat keine Kenntnis von `InMemoryCardRepository` oder einer anderen Plugin-Klasse.

#figure(
  image("../assets/diagrams/Positiv-Beispiel-CreateCardUseCase.png"),
  caption: [CreateCardUseCase: Abhängigkeiten zeigen nach innen (Domain)],
)

Analyse: `CreateCardUseCase` (Application-Schicht) hängt ab von `CardRepository` und `DeckRepository` (Domain-Schicht): Abhängigkeit zeigt nach innen. Niemand aus der Domain hängt von `CreateCardUseCase` ab. Die konkrete Implementierung `InMemoryCardRepository` (Plugin) kennt `CreateCardUseCase` nicht: die Dependency Rule ist eingehalten.

=== Negativ-Beispiel: DeckMenu

`DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:12-13`) importiert konkrete Plugin-Klassen (`RandomRepetitionAlgorithm`, `SpacedRepetitionAlgorithm`) statt sich nur auf das Domain-Interface `RepetitionAlgorithm` zu verlassen.

#figure(
  image("../assets/diagrams/Negativ-Beispiel-DeckMenu.png"),
  caption: [DeckMenu: Verletzung der Dependency Rule durch direkte Plugin-Abhängigkeit],
)

Analyse: `DeckMenu` (Adapter-Schicht) hängt direkt von `RandomRepetitionAlgorithm` und `SpacedRepetitionAlgorithm` (Plugin-Schicht) ab. Die Adapter-Schicht referenziert die Plugin-Schicht: das verletzt die Dependency Rule, da beide äußere Schichten sind und keine davon von der anderen abhängen sollte. Lösung: `DeckMenu` sollte eine `List<RepetitionAlgorithm>` injiziert bekommen und das Menü dynamisch aus `getName()` aufbauen.

```java
// DeckMenu.java:12-13: Verletzung: Import konkreter Plugin-Typen
import com.cardrep.plugin.algorithm.RandomRepetitionAlgorithm;
import com.cardrep.plugin.algorithm.SpacedRepetitionAlgorithm;
```

== Analyse der Schichten

=== Schicht: Domain: `Deck`

#figure(
  image("../assets/diagrams/Schicht-Domain-Deck.png"),
  caption: [Deck als zentrales Domain-Objekt mit Strategy und Observer],
)

Aufgabe: `Deck` ist das zentrale Aggregate Root der Domain-Schicht. Es verwaltet eine Sammlung von Cards, delegiert die Kartenauswahl an eine konfigurierbare Strategie und benachrichtigt Observer bei Änderungen.

Einordnung: Domain-Schicht, weil `Deck` ausschließlich Geschäftslogik enthält (Kartenauswahl, Statistikberechnung, Observer-Benachrichtigung) und keine Abhängigkeiten zu äußeren Schichten hat. Alle Abhängigkeiten (`RepetitionAlgorithm`, `DeckStatsObserver`) sind als Interfaces in der Domain definiert.

=== Schicht: Application: `LearnCardUseCase`

#figure(
  image("../assets/diagrams/Schicht-Application-LearnCardUseCase.png"),
  caption: [LearnCardUseCase: Orchestrierung des Lernvorgangs],
)

Aufgabe: `LearnCardUseCase` orchestriert den Lernvorgang: Es lädt die Karte, zeichnet die Review auf (mit Schwierigkeitsgrad), speichert die aktualisierte Karte und benachrichtigt Observer über Deck-Statistikänderungen.

Einordnung: Application-Schicht, weil es einen konkreten Anwendungsfall orchestriert und dabei ausschließlich Domain-Interfaces verwendet. Es enthält keine Geschäftslogik selbst (die liegt in `Card.recordReview()` und `Deck.notifyObservers()`), sondern koordiniert den Ablauf zwischen Domain-Objekten.
