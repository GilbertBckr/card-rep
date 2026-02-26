# Card Repetition (card-rep)

A terminal-based spaced repetition learning application (similar to Anki) built in **Java 17** with **Maven**. Users can create collections, organize decks, add cards, and learn them in interactive sessions using configurable scheduling algorithms.

## Table of Contents

1. [Introduction](#1-introduction)
2. [Clean Architecture](#2-clean-architecture)
3. [SOLID](#3-solid)
4. [Further Principles (GRASP, DRY)](#4-further-principles)
5. [Unit Tests](#5-unit-tests)
6. [Domain-Driven Design](#6-domain-driven-design)
7. [Refactoring](#7-refactoring)
8. [Design Patterns](#8-design-patterns)

---

## 1. Introduction

### Purpose

Card Repetition is a CLI application that implements the core concepts of spaced repetition learning. Users organize their study material into **Collections** (hierarchical folders) containing **Decks**, which hold **Cards** (front/back flashcards). During a **Learning Session**, cards are presented based on a **Repetition Algorithm** (Strategy Pattern), and the user rates the perceived **Difficulty**. The system tracks **CardStats** and **DeckStats**, notifying observers (Observer Pattern) when statistics change.

### How to Build and Run

```bash
# Build
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.cardrep.adapter.cli.CardRepApp"

# Generate test coverage report
mvn test jacoco:report
# Coverage report at: target/site/jacoco/index.html
```

### Technology Stack

| Component        | Technology              |
|------------------|-------------------------|
| Language         | Java 17                 |
| Build Tool       | Maven                   |
| Testing          | JUnit 5.10.2            |
| Mocking          | Mockito 5.11.0          |
| Code Coverage    | JaCoCo 0.8.11           |
| Persistence      | In-Memory (HashMap)     |

---

## 2. Clean Architecture

The project follows **Clean Architecture** with four distinct layers. Dependencies point inward: the domain layer has no dependencies on any other layer; the application layer depends only on the domain; infrastructure and adapter layers depend on the inner layers but never the reverse.

### Layer Diagram

```
┌──────────────────────────────────────────────────────┐
│                  Adapter (CLI)                       │
│  CardRepApp, MainMenu, CardMenu, DeckMenu,           │
│  CollectionMenu, LearningSession, MenuHelper         │
├──────────────────────────────────────────────────────┤
│               Infrastructure                         │
│  InMemoryCardRepository, InMemoryDeckRepository,     │
│  InMemoryCollectionRepository,                       │
│  RandomRepetitionAlgorithm, SpacedRepetitionAlgorithm│
│  DeckStatsLogger                                     │
├──────────────────────────────────────────────────────┤
│                Application                           │
│  CreateCardUseCase, ModifyCardUseCase, DeleteCardUC,  │
│  CreateDeckUseCase, ModifyDeckUseCase, DeleteDeckUC,  │
│  NextCardUseCase, LearnCardUseCase,                  │
│  CreateCollectionUC, ModifyCollectionUC, DeleteCollUC │
├──────────────────────────────────────────────────────┤
│                   Domain                             │
│  Card, Deck, Collection, RootCollection,             │
│  CardContent, CardStats, DeckStats, Difficulty,      │
│  CardRepository, DeckRepository,                     │
│  CollectionRepository, RepetitionAlgorithm,          │
│  DeckStatsObserver                                   │
└──────────────────────────────────────────────────────┘
```

### Dependency Rule

The **Dependency Rule** states that source code dependencies must point inward — outer layers may depend on inner layers, but never the reverse.

**Positive Example — Use cases depend on domain interfaces, not implementations:**

The `CreateCardUseCase` (`src/main/java/com/cardrep/application/card/CreateCardUseCase.java`) depends on `CardRepository` and `DeckRepository`, which are interfaces defined in the domain layer (`src/main/java/com/cardrep/domain/repository/`). It has no knowledge of `InMemoryCardRepository` or any infrastructure class. This allows swapping the storage mechanism without changing any application logic.

**Negative Example — DeckMenu depends on concrete algorithm types:**

The `DeckMenu` class (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:12-13`) imports and depends on `RandomRepetitionAlgorithm` and `SpacedRepetitionAlgorithm` (concrete infrastructure classes) rather than using only the `RepetitionAlgorithm` interface. This violates the Dependency Rule because the adapter layer directly references the infrastructure layer. A better approach would be to inject a `Map<String, RepetitionAlgorithm>` or a factory, so `DeckMenu` only depends on the domain interface.

```java
// DeckMenu.java:12-13 — Violation: importing concrete infrastructure types
import com.cardrep.infrastructure.algorithm.RandomRepetitionAlgorithm;
import com.cardrep.infrastructure.algorithm.SpacedRepetitionAlgorithm;
```

---

## 3. SOLID

### 3.1 Single Responsibility Principle (SRP)

> A class should have only one reason to change.

**Positive Example — Each use case has a single responsibility:**

Each use case class in the application layer handles exactly one operation. For instance, `CreateCardUseCase` (`src/main/java/com/cardrep/application/card/CreateCardUseCase.java`) is solely responsible for creating a card and adding it to a deck. It does not handle card deletion, modification, or learning. If the card creation logic changes, only this class needs to be modified.

**Negative Example — DeckMenu has multiple responsibilities:**

`DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java`) handles CRUD operations for decks, algorithm selection, stats display, AND user input parsing. It has four distinct reasons to change: (1) deck operations change, (2) algorithm selection changes, (3) stats display format changes, (4) input handling changes. A more SRP-compliant design would separate the algorithm selection into its own class.

### 3.2 Open/Closed Principle (OCP)

> Software entities should be open for extension but closed for modification.

**Positive Example — RepetitionAlgorithm is open for extension:**

New scheduling algorithms can be added by implementing the `RepetitionAlgorithm` interface (`src/main/java/com/cardrep/domain/service/RepetitionAlgorithm.java`) without modifying existing code. We already have `RandomRepetitionAlgorithm` and `SpacedRepetitionAlgorithm`. A third algorithm (e.g., `LeitnerAlgorithm`) could be added by simply creating a new class that implements the interface.

**Negative Example — DeckMenu.selectAlgorithm() requires modification for new algorithms:**

The `selectAlgorithm()` method in `DeckMenu` (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:183-198`) uses a hardcoded switch statement listing available algorithms. Adding a new algorithm requires modifying this method, violating OCP:

```java
// DeckMenu.java — must be modified to add a new algorithm
return switch (choice) {
    case "1" -> spacedAlgorithm;
    case "2" -> randomAlgorithm;
    default -> spacedAlgorithm;
};
```

### 3.3 Liskov Substitution Principle (LSP)

> Subtypes must be substitutable for their base types without altering program correctness.

**Positive Example — RepetitionAlgorithm implementations are substitutable:**

Both `RandomRepetitionAlgorithm` and `SpacedRepetitionAlgorithm` can be substituted wherever a `RepetitionAlgorithm` is expected. The `Deck` class (`src/main/java/com/cardrep/domain/model/Deck.java:104-109`) calls `repetitionAlgorithm.selectNextCard(cards)` without knowing which concrete implementation is being used. Both implementations honor the contract: they accept a list of cards and return a card (or `null` if the list is empty).

**Positive Example — RootCollection extends Collection correctly:**

`RootCollection` (`src/main/java/com/cardrep/domain/model/RootCollection.java`) extends `Collection` and can be used anywhere a `Collection` is expected. It overrides `setName()` to throw `UnsupportedOperationException`, which could be seen as an LSP concern; however, this is an intentional design decision documented in the ubiquitous language — the root collection has a fixed identity and name. Callers navigating the collection tree treat it like any other collection for reading purposes.

### 3.4 Interface Segregation Principle (ISP)

> Clients should not be forced to depend on interfaces they do not use.

**Positive Example — Repository interfaces are focused:**

The repository interfaces (`CardRepository`, `DeckRepository`, `CollectionRepository`) in `src/main/java/com/cardrep/domain/repository/` each define only the operations relevant to their aggregate. `CollectionRepository` does not have `findAll()` or `existsById()` because collections are navigated via the tree structure starting from `getRootCollection()`. This keeps each interface minimal and focused.

**Positive Example — DeckStatsObserver has a single method:**

The `DeckStatsObserver` interface (`src/main/java/com/cardrep/domain/model/DeckStatsObserver.java`) defines only one method: `onDeckStatsChanged(Deck, DeckStats)`. Observers are not forced to implement unrelated methods. This is a clean, focused interface.

### 3.5 Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Positive Example — Use cases depend on repository interfaces:**

All use cases depend on repository interfaces defined in the domain layer, not on concrete implementations. For example, `DeleteDeckUseCase` (`src/main/java/com/cardrep/application/deck/DeleteDeckUseCase.java`) accepts `DeckRepository`, `CardRepository`, and `CollectionRepository` in its constructor — all are interfaces. The concrete `InMemoryCardRepository` is injected at the composition root (`CardRepApp.java`).

**Negative Example — DeckMenu depends on concrete algorithm classes:**

As noted in the Dependency Rule section, `DeckMenu` directly depends on `RandomRepetitionAlgorithm` and `SpacedRepetitionAlgorithm` (concrete classes) rather than depending on the `RepetitionAlgorithm` abstraction. The constructor signature (`src/main/java/com/cardrep/adapter/cli/DeckMenu.java:35-49`) requires these concrete types:

```java
// Violation: constructor parameters are concrete types, not abstractions
private final RandomRepetitionAlgorithm randomAlgorithm;
private final SpacedRepetitionAlgorithm spacedAlgorithm;
```

A DIP-compliant approach would inject a `List<RepetitionAlgorithm>` and dynamically build the selection menu from `getName()`.

---

## 4. Further Principles

### 4.1 GRASP — Low Coupling

> Assign responsibilities so that coupling remains low.

**Positive Example:**

The domain layer has **zero dependencies** on any outer layer. `Card`, `Deck`, `Collection`, and all value objects are self-contained. The `RepetitionAlgorithm` and `DeckStatsObserver` interfaces are defined in the domain, allowing infrastructure implementations to be swapped without any domain changes. This low coupling means the domain can be tested in isolation without mocking frameworks.

**Negative Example:**

The `DeckMenu` class is tightly coupled to concrete algorithm implementations (`RandomRepetitionAlgorithm`, `SpacedRepetitionAlgorithm`). Adding or removing an algorithm requires changes in `DeckMenu`, which is an adapter-layer class that should ideally be decoupled from infrastructure details.

### 4.2 GRASP — High Cohesion

> Assign responsibilities so that cohesion remains high.

**Positive Example:**

Each use case class is highly cohesive — it encapsulates exactly one business operation with all the steps needed to complete it. `CreateCardUseCase` validates the deck exists, creates the card, saves it, adds it to the deck, and saves the deck. All methods in this class serve the single purpose of card creation.

**Positive Example:**

The `CardStats` value object (`src/main/java/com/cardrep/domain/model/CardStats.java`) groups all review-related data and behavior together: review history, total reviews count, last difficulty, and last review time. Everything in the class relates to the concept of "card review statistics."

### 4.3 DRY — Don't Repeat Yourself

> Every piece of knowledge must have a single, unambiguous, authoritative representation.

**Before Refactoring (Violation):**

The `selectDeck()` method was duplicated in `CardMenu` and `LearningSession`. Similarly, `selectCollection()` was duplicated in `DeckMenu` and `CollectionMenu`. Each copy had the same logic: fetch items from a repository, display a numbered list, parse user input, and return the selected ID.

```java
// CardMenu.java (before) — duplicated selectDeck()
private String selectDeck() {
    List<Deck> decks = deckRepository.findAll();
    if (decks.isEmpty()) { ... }
    for (int i = 0; i < decks.size(); i++) {
        System.out.println("  " + (i + 1) + ". " + decks.get(i).getName());
    }
    // ... parse input, return ID
}

// LearningSession.java (before) — nearly identical selectDeck()
private String selectDeck() {
    List<Deck> decks = deckRepository.findAll();
    if (decks.isEmpty()) { ... }
    for (int i = 0; i < decks.size(); i++) {
        System.out.println("  " + (i + 1) + ". " + deck.getName() + " (" + deck.getCards().size() + " cards)");
    }
    // ... parse input, return ID
}
```

**After Refactoring (DRY Applied):**

The duplicated logic was extracted into `MenuHelper` (`src/main/java/com/cardrep/adapter/cli/MenuHelper.java`), which provides a generic `selectFromList()` method using Java generics and `Function` parameters. The specific `selectDeck()`, `selectCollection()`, etc. are now single methods in `MenuHelper` that delegate to this generic helper. All menu classes share the same `MenuHelper` instance.

See commit `95a622a` for this refactoring.

---

## 5. Unit Tests

### Overview

The project contains **61 unit tests** across 8 test classes covering the domain, application, and infrastructure layers.

| Test Class | Tests | Layer | Approach |
|------------|-------|-------|----------|
| `CardTest` | 8 | Domain | Direct instantiation |
| `CardContentTest` | 8 | Domain | Direct instantiation |
| `DeckTest` | 13 | Domain | Direct instantiation + fakes |
| `CollectionTest` | 7 | Domain | Direct instantiation |
| `CardUseCaseTest` | 5 | Application | Mockito mocks |
| `DeckUseCaseTest` | 6 | Application | Mockito mocks |
| `InMemoryCardRepositoryTest` | 6 | Infrastructure | Fake (real implementation) |
| `RepetitionAlgorithmTest` | 8 | Infrastructure | Seeded Random for determinism |

### ATRIP Qualities

The tests follow the **ATRIP** qualities:

- **Automatic:** All tests run automatically via `mvn test` with no manual intervention. JUnit 5 discovers and executes all tests. JaCoCo generates coverage reports automatically.

- **Thorough:** Tests cover both happy paths and edge cases. For example, `CardTest` tests valid creation, null front/back rejection, blank text rejection, review recording, and difficulty tracking. `DeckTest` covers adding cards, duplicate card rejection, removing nonexistent cards, empty deck behavior, stats computation, and observer notification.

- **Repeatable:** Tests produce the same result on every run. The `RepetitionAlgorithmTest` uses `new Random(42)` (a seeded random) to make the `RandomRepetitionAlgorithm` deterministic. No tests depend on external state, timing, or order.

- **Independent:** Each test method is self-contained with its own setup. `@BeforeEach` creates fresh instances for every test. Tests do not depend on execution order and do not share mutable state.

- **Professional:** Tests follow naming conventions (`methodName_condition_expectedResult`), use clear assertions with descriptive messages, and are organized by layer. Test code follows the same quality standards as production code.

### Mocks vs. Fakes

**Mocks (Mockito):** Used in `CardUseCaseTest` and `DeckUseCaseTest` to isolate use cases from repository implementations. Mockito's `@Mock` annotation creates mock repositories, and `when(...).thenReturn(...)` defines expected behavior. `verify(...)` checks that the correct repository methods were called.

```java
// DeckUseCaseTest.java — Mock example
@Mock private DeckRepository deckRepository;
@Mock private CardRepository cardRepository;

@Test
void deleteDeck_shouldDeleteAllCardsAndDeck() {
    when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
    deleteDeckUseCase.execute(deck.getId(), collectionId);
    verify(cardRepository).deleteById(card1.getId());  // verify interaction
}
```

**Fakes (Real Implementation):** Used in `InMemoryCardRepositoryTest`, which tests the actual `InMemoryCardRepository` — a real, working in-memory implementation (HashMap-backed). This is a fake rather than a mock because it has real behavior and state. Also used in `RepetitionAlgorithmTest` where real algorithm implementations are tested with seeded `Random` for determinism.

```java
// InMemoryCardRepositoryTest.java — Fake example
private InMemoryCardRepository repository;

@BeforeEach
void setUp() {
    repository = new InMemoryCardRepository();  // real implementation, not a mock
}
```

### Code Coverage

Coverage is measured via **JaCoCo**. The domain and application layers have the highest coverage since they contain the core business logic and are tested most thoroughly. The adapter (CLI) layer has lower coverage because it primarily handles user I/O, which is tested manually. Run `mvn test jacoco:report` and open `target/site/jacoco/index.html` for the full report.

---

## 6. Domain-Driven Design

### Ubiquitous Language

The project uses a documented **Ubiquitous Language** (see `ubiquoutous_language.md`) that defines all domain terms. These terms are used consistently throughout the codebase — in class names, method names, variable names, and comments:

| Domain Term | Java Representation | Type |
|-------------|-------------------|------|
| Collection | `Collection.java` | Entity / Aggregate Root |
| Deck | `Deck.java` | Entity / Aggregate Root |
| Card | `Card.java` | Entity |
| CardFront / CardBack | `CardContent.java` (used for both) | Value Object |
| CardContent | `CardContent.java` | Value Object |
| CardStats | `CardStats.java` | Value Object |
| DeckStats | `DeckStats.java` | Value Object |
| Difficulty | `Difficulty.java` (enum) | Value Object |
| RootCollection | `RootCollection.java` | Entity |
| Repetition Algorithm | `RepetitionAlgorithm.java` | Domain Service Interface |
| User | Implicit (single-user app) | — |

### Entities

Entities are objects with a unique identity that persists across state changes. They use identity-based `equals()` and `hashCode()`.

- **Card** (`src/main/java/com/cardrep/domain/model/Card.java`) — identified by UUID. Content can change via `modify()`, stats change via `recordReview()`, but the identity remains the same.
- **Deck** (`src/main/java/com/cardrep/domain/model/Deck.java`) — identified by UUID. Owns a mutable list of cards and a configurable algorithm.
- **Collection** (`src/main/java/com/cardrep/domain/model/Collection.java`) — identified by UUID. Contains child collections and decks (tree structure).

### Value Objects

Value Objects have no identity and are defined by their attributes. They are immutable.

- **CardContent** — text + optional image path. Two `CardContent` instances with the same text and image are equal.
- **CardStats** — review history with `withNewReview()` returning a new instance (functional immutability).
- **DeckStats** — aggregated statistics. All fields are `final`, no setters.
- **Difficulty** — enum with four values: `EASY`, `MEDIUM`, `HARD`, `AGAIN`.

### Aggregates

- **Deck** is an Aggregate Root that owns its Cards. As defined in the ubiquitous language: *"a card has to belong to at least one deck"* and *"when a deck is deleted all of its child Cards will be deleted as well."*
- **Collection** is an Aggregate Root that owns child Collections and Decks. It enforces the business invariant that names must be unique within a parent (case-insensitive).

### Repositories

Repository interfaces are defined in the domain layer, following DDD's principle that the domain defines the contracts for persistence:

- `CardRepository` — CRUD for cards
- `DeckRepository` — CRUD for decks
- `CollectionRepository` — save, find, delete, plus `getRootCollection()`

Implementations (`InMemoryCardRepository`, etc.) are in the infrastructure layer, keeping the domain free of persistence technology details.

---

## 7. Refactoring

### Refactoring 1: Extract Class — MenuHelper (DRY)

**Commit:** `95a622a`

**Problem:** The `selectDeck()` method was duplicated in `CardMenu` and `LearningSession`. The `selectCollection()` method was duplicated in `DeckMenu` and `CollectionMenu`. Each copy followed the same pattern: fetch a list, display it numbered, parse user input, return the selected ID. This violated the DRY principle — approximately 120 lines of duplicated code across 4 classes.

**Before (CardMenu.java):**
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

**After (MenuHelper.java):**
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

public String selectDeck() {
    List<Deck> decks = deckRepository.findAll();
    if (decks.isEmpty()) {
        System.out.println("No decks available. Create a deck first.");
        return null;
    }
    return selectFromList(decks, "Available decks",
            deck -> deck.getName() + " (" + deck.getCards().size() + " cards)",
            Deck::getId);
}
```

**Result:** 4 classes now delegate to `MenuHelper`. The generic `selectFromList()` method can be reused for any future list-based selection. Net change: +190 lines (new file), -208 lines (removed duplication) = **18 fewer lines overall** with better structure.

### Refactoring 2: Extract Method — Deck.computeStats()

**Commit:** `7c1afb9`

**Problem:** The `computeStats()` method in `Deck` was a single monolithic method with a loop containing nested if-else chains for counting cards by difficulty. It mixed filtering logic (which cards are reviewed) with counting logic (categorizing by difficulty).

**Before (Deck.java):**
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

**After (Deck.java):**
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

**Result:** The method is now composed of clearly named helper methods. `getReviewedCards()` handles filtering; `countByLastDifficulty()` handles categorization. Each method has a single responsibility and can be understood independently. The declarative stream-based approach replaces the imperative loop with mutable counters.

---

## 8. Design Patterns

### 8.1 Strategy Pattern — RepetitionAlgorithm

The **Strategy Pattern** allows the scheduling algorithm to be selected and swapped at runtime. Each deck can use a different algorithm without any changes to the `Deck` class itself.

**Participants:**
- **Strategy (Interface):** `RepetitionAlgorithm` (`src/main/java/com/cardrep/domain/service/RepetitionAlgorithm.java`)
- **Concrete Strategy A:** `RandomRepetitionAlgorithm` (`src/main/java/com/cardrep/infrastructure/algorithm/RandomRepetitionAlgorithm.java`)
- **Concrete Strategy B:** `SpacedRepetitionAlgorithm` (`src/main/java/com/cardrep/infrastructure/algorithm/SpacedRepetitionAlgorithm.java`)
- **Context:** `Deck` (`src/main/java/com/cardrep/domain/model/Deck.java`)

**UML Class Diagram:**

```
┌─────────────────────────────────┐
│            Deck                 │
│         (Context)               │
├─────────────────────────────────┤
│ - repetitionAlgorithm           │
├─────────────────────────────────┤
│ + getNextCard(): Card           │
│ + setRepetitionAlgorithm(algo)  │
└──────────────┬──────────────────┘
               │ uses
               ▼
┌─────────────────────────────────┐
│   <<interface>>                 │
│   RepetitionAlgorithm           │
├─────────────────────────────────┤
│ + selectNextCard(cards): Card   │
│ + getName(): String             │
└──────────┬──────────┬───────────┘
           │          │
     ┌─────┘          └──────┐
     ▼                       ▼
┌──────────────────┐  ┌──────────────────────┐
│ Random           │  │ SpacedRepetition     │
│ Repetition       │  │ Algorithm            │
│ Algorithm        │  │                      │
├──────────────────┤  ├──────────────────────┤
│ - random: Random │  │                      │
├──────────────────┤  ├──────────────────────┤
│ + selectNextCard │  │ + selectNextCard     │
│ + getName        │  │ - calculatePriority  │
└──────────────────┘  │ + getName            │
                      └──────────────────────┘
```

**How it works:**

1. When a `Deck` is created, a `RepetitionAlgorithm` is passed to it
2. When the user starts a learning session, `Deck.getNextCard()` delegates to `repetitionAlgorithm.selectNextCard(cards)`
3. The algorithm can be changed at runtime via `Deck.setRepetitionAlgorithm()`
4. `RandomRepetitionAlgorithm` selects a random card from the list
5. `SpacedRepetitionAlgorithm` selects the card with the lowest priority score (unreviewed cards first, then cards rated `AGAIN`, `HARD`, etc.)

### 8.2 Observer Pattern — DeckStatsObserver

The **Observer Pattern** notifies interested parties whenever deck statistics change (e.g., when a card is added, removed, or reviewed). This decouples the notification logic from the core domain model.

**Participants:**
- **Subject:** `Deck` (`src/main/java/com/cardrep/domain/model/Deck.java`)
- **Observer (Interface):** `DeckStatsObserver` (`src/main/java/com/cardrep/domain/model/DeckStatsObserver.java`)
- **Concrete Observer:** `DeckStatsLogger` (`src/main/java/com/cardrep/infrastructure/observer/DeckStatsLogger.java`)

**UML Class Diagram:**

```
┌──────────────────────────────────┐
│             Deck                 │
│           (Subject)              │
├──────────────────────────────────┤
│ - observers: List<Observer>      │
├──────────────────────────────────┤
│ + addObserver(observer)          │
│ + removeObserver(observer)       │
│ + notifyObservers()              │
│ + addCard(card)    ──triggers──► │
│ + removeCard(id)   ──triggers──► │
└──────────────┬───────────────────┘
               │ notifies
               ▼
┌──────────────────────────────────┐
│        <<interface>>             │
│      DeckStatsObserver           │
├──────────────────────────────────┤
│ + onDeckStatsChanged(deck,stats) │
└──────────────┬───────────────────┘
               │
               ▼
┌──────────────────────────────────┐
│       DeckStatsLogger            │
│     (Concrete Observer)          │
├──────────────────────────────────┤
│ + onDeckStatsChanged(deck,stats) │
│   → prints to System.out        │
└──────────────────────────────────┘
```

**How it works:**

1. `DeckStatsLogger` is registered as an observer on a deck via `deck.addObserver(statsLogger)`
2. When `Deck.addCard()` or `Deck.removeCard()` is called, they trigger `notifyObservers()`
3. `notifyObservers()` computes the current `DeckStats` and calls `onDeckStatsChanged(this, stats)` on each registered observer
4. The `LearnCardUseCase` also calls `deck.notifyObservers()` after recording a review
5. `DeckStatsLogger` logs the updated stats to the console
6. Additional observers (e.g., a persistence observer, a UI updater) can be added without modifying the `Deck` class
