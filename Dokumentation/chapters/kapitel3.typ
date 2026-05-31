= SOLID

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
