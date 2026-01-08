# Ubiquoutus Language

## Core Domain

Collection: A set of Decks and Collections, a Collection has a name unique to its parent container (collection)
Deck: A set of Cards, one card can belong to multiple decks, a card has to belong to at least one deck, a deck has a name unique to its parent collection

Card:
    Is the smallest unit of learnable content consists of a front and a back:
        CardFront
        CardBack


User: The person interacting with the application, we assume that there is only one person interacting with the application on one operating system user account
Difficulty: Ranking of a cards perceived difficulty on a scale of easy, medium, hard, again CardStats: Information about the usage in regards to a specific card, in a series storing the interaction point in time, the rated and the rated difficulty DeckStats: The deck stats show aggregate about the learning stats of the cards in the deck, like the number of cards seen etc.
CardFront: Front side of a Card usually the question that should be answered on the CardBack
CardBack: Information that should be remembered based on the corresponding CardFront

CardContent: The content a card side, it can consist of a combination of text and image

## Card Repetition SubDomain
The domain taking care of how the cards are scheduled for the user when they are learning

Repetition Algorithm: A set of rules defining the order (and to a limited degree the timing) of cards being shown in dynamic fashion

*More algorithms to be defined soon* 

## UI Rendering SubDomain


# Use Cases
Creation of Card:
The user wants to create a card to learn from it, at the point of creation the front and the back have to be specified as well as the Deck encompassing the Card



Modification of Card
The users should be able to update the front and back of the card, the stats should remain the same as before


Deletion of Card
user should be able to delete the card, the information and associated stats should be deleted too, TODO: what happens to Deck Stats?


Assigning Card to Deck
A card should be assigned to a deck this means it is now in the deck TODO: deck stats when moving card



Creation of Deck

"learning" of Card


Creation of Deck

Modification of Deck

Deletion of Deck

Creation of Collection

Modification of Collection

Deletion of Collection
