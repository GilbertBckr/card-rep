# Ubiquitous Language

Core Domain

Collection: A set of Decks and Collections, a Collection has a name unique to its parent container (collection)
Deck: A set of Cards, one card can belong to only one deck, a card has to belong to at least one deck, a deck has a name unique to its parent collection

RootCollection: The Collection at the root of the collection tree structure, it cannot be removed and is already there in the beginning

Card:
  Is the smallest unit of learnable content and consists of a front and a back:
    CardFront
    CardBack

User: The person interacting with the application, we assume that there is only one person interacting with the application on one operating system user account
Difficulty: Ranking of a card’s perceived difficulty on a scale of easy, medium, hard, fail (again)

CardStats: Information about the usage in regard to a specific card, in a series storing the interaction point in time, the rating and the rated difficulty

DeckStats: The deck stats show aggregates about the learning stats of the cards in the deck, like the number of cards seen etc.

CardFront: Front side of a Card intended for the question that should be answered on the CardBack

CardBack: Information that shall be remembered based on the corresponding CardFront

CardContent: The content of a card side, it can consist of a combination of text and image

Card Learning SubDomain

The domain taking care of how the cards are scheduled for the user when they are learning

Repetition Algorithm: A set of rules defining the order (and to a limited degree the timing) of cards being shown in dynamic fashion

More algorithms to be defined soon

Entities

Collection: CollectionID, Name, ChildCollections, ChildDecks

Deck: DeckID, Name, CardRefs, DeckStats

Card: CardID, Front (CardContent), Back (CardContent), CardStats

Value Objects

CardContent

DeckStats

CardStats

Difficulty

Use Cases

Creation of Card:
The user wants to create a card to learn from it, at the point of creation the front and the back have to be specified as well as the Deck encompassing the Card

Modification of Card
The users should be able to update the front and back of the card, the stats should remain the same as before

Deletion of Card
User should be able to delete the card, the information and associated stats should be deleted too

Assigning Card to Deck
An existing card should be assigned to a deck; this means it is now in the deck, the deck’s stats need to be updated

Creation of Deck
A new Deck can be created inside of a specific collection, with a unique name up to its parent. The user can choose the scheduling algorithm; otherwise, a default algorithm will be used.

"Learning" of Card
The user is shown the front of a card, he can then reveal the back of the card, and is then shown the back of a card, where he can rate the perceived difficulty

Next Card Function
For a given Deck the user can request the next card that they should learn, based on the chosen scheduling algorithm of that Deck.

Modification of Deck
The name and scheduling strategy of a deck can be changed

Deletion of Deck
When a deck is deleted all of its child Cards will be deleted as well since they need to have at least one Deck

Creation of Collection
A Collection can be created inside of another Collection (including the RootCollection) with a unique name up to its parent

Modification of Collection
A collection name can be changed, if it does not violate the uniqueness property of the name

Deletion of Collection
A Collection can be deleted, deleting all its child content (Deck, Collection)

