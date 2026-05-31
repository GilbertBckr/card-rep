= Domain Driven Design

== Ubiquitous Language

#table(
  columns: (auto, 1fr, 1fr),
  align: (left, left, left),
  table.header([*Bezeichnung*], [*Bedeutung*], [*Begründung*]),
  [*Card*], [Eine Lernkarte mit Vorderseite (Frage) und Rückseite (Antwort), die Review-Statistiken trackt], [Zentrales Domänenkonzept: jeder Benutzer interagiert mit Karten beim Lernen. Direkt als Java-Klasse `Card` im Code],
  [*Deck*], [Eine Sammlung von Karten mit einem konfigurierbaren Wiederholungsalgorithmus], [Organisationseinheit für Karten: ein Deck wird als Ganzes gelernt. Eigenständiges Aggregate Root im Code],
  [*Difficulty*], [Die vom Benutzer wahrgenommene Schwierigkeit einer Karte (EASY, MEDIUM, HARD, AGAIN)], [Bestimmt die Wiederholungsfrequenz: ist der zentrale Input des Spaced-Repetition-Systems. Als Enum `Difficulty` implementiert],
  [*Collection*], [Ein hierarchischer Ordner, der Decks und weitere Collections enthält], [Ermöglicht beliebig tiefe Organisationsstrukturen (z.B. "Informatik > Algorithmen > Sortierung"). Als Baum-Struktur mit Rekursion implementiert],
)

== Entities

=== Card

#figure(
  image("../assets/diagrams/Entities-Card.png"),
  caption: [Card als Entity mit UUID-Identität],
)

Beschreibung: `Card` (`src/main/java/com/cardrep/domain/model/Card.java`) repräsentiert eine Lernkarte mit eindeutiger UUID-Identität. Der Inhalt (front/back) kann sich über `modify()` ändern, Statistiken ändern sich über `recordReview()`, aber die Identität bleibt gleich.

Begründung: Card ist eine Entity, weil sie eine eindeutige Identität besitzt, die über ihren Lebenszyklus hinweg bestehen bleibt. Zwei Karten mit identischem Text sind trotzdem verschiedene Karten (unterschiedliche IDs, unterschiedliche Statistiken). `equals()` und `hashCode()` basieren ausschließlich auf der ID.

== Value Objects

=== CardContent

#figure(
  image("../assets/diagrams/ValueObject-CardContent.png"),
  caption: [CardContent als immutables Value Object],
)

Beschreibung: `CardContent` (`src/main/java/com/cardrep/domain/model/CardContent.java`) repräsentiert den Textinhalt (plus optionalem Bild) einer Kartenseite.

Begründung: CardContent ist ein Value Object, weil es keine eigene Identität besitzt: zwei `CardContent`-Instanzen mit gleichem Text und gleichem Bildpfad sind vollständig austauschbar (gleich im Sinne von `equals()`). Es ist immutable (alle Felder `final`, keine Setter), und wird über seine Attribute definiert, nicht über eine ID.

== Repositories

=== CardRepository

#figure(
  image("../assets/diagrams/Repo-CardRepository.png"),
  caption: [CardRepository: Interface in Domain, Implementierung in Plugin],
)

Beschreibung: `CardRepository` (`src/main/java/com/cardrep/domain/repository/CardRepository.java`) definiert den Vertrag für die Persistenz von Cards. Die Implementierung `InMemoryCardRepository` nutzt eine HashMap.

Begründung: Repositories sind der DDD-Mechanismus, um die Domain von Persistenzdetails zu entkoppeln. Das Interface ist in der Domain-Schicht definiert (Dependency Inversion), die Implementierung in der Plugin-Schicht. So kann die Speichertechnologie gewechselt werden (z.B. zu einer Datenbank), ohne die Domain oder Application-Schicht zu ändern.

== Aggregates

=== Deck (Aggregate Root)

#figure(
  image("../assets/diagrams/Aggregate-Deck.png"),
  caption: [Deck als Aggregate Root mit Konsistenzgrenze],
)

Beschreibung: `Deck` ist Aggregate Root und kontrolliert den Zugriff auf seine `Card`-Objekte. Definiert in der Ubiquitous Language: _"Eine Karte muss zu genau einem Deck gehören"_ und _"Wenn ein Deck gelöscht wird, werden alle zugehörigen Karten ebenfalls gelöscht."_

Begründung: Deck ist ein Aggregate, weil es eine Konsistenzgrenze definiert: Karten können nur über das Deck hinzugefügt/entfernt werden, nicht direkt. Das Deck erzwingt Invarianten (keine doppelten Card-IDs) und garantiert kaskadierendes Löschen. External Entities referenzieren Cards nur über das Deck, was transaktionale Konsistenz sichert.
