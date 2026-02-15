package com.cardrep.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Collection entity.
 */
class CollectionTest {

    @Test
    void createCollection_withValidName_shouldSucceed() {
        Collection collection = Collection.create("Languages");

        assertNotNull(collection.getId());
        assertEquals("Languages", collection.getName());
        assertTrue(collection.getChildCollections().isEmpty());
        assertTrue(collection.getChildDecks().isEmpty());
    }

    @Test
    void createCollection_withNullName_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> Collection.create(null));
    }

    @Test
    void addChildCollection_withUniqueName_shouldSucceed() {
        Collection parent = Collection.create("Parent");
        Collection child = Collection.create("Child");

        parent.addChildCollection(child);

        assertEquals(1, parent.getChildCollections().size());
    }

    @Test
    void addChildCollection_withDuplicateName_shouldThrow() {
        Collection parent = Collection.create("Parent");
        Collection child1 = Collection.create("Child");
        Collection child2 = Collection.create("Child");
        parent.addChildCollection(child1);

        assertThrows(IllegalArgumentException.class,
                () -> parent.addChildCollection(child2));
    }

    @Test
    void isCollectionNameUnique_shouldReturnCorrectly() {
        Collection parent = Collection.create("Parent");
        parent.addChildCollection(Collection.create("Existing"));

        assertFalse(parent.isCollectionNameUnique("Existing"));
        assertFalse(parent.isCollectionNameUnique("existing")); // case-insensitive
        assertTrue(parent.isCollectionNameUnique("New"));
    }

    @Test
    void removeChildCollection_existing_shouldSucceed() {
        Collection parent = Collection.create("Parent");
        Collection child = Collection.create("Child");
        parent.addChildCollection(child);

        parent.removeChildCollection(child.getId());

        assertTrue(parent.getChildCollections().isEmpty());
    }

    @Test
    void removeChildCollection_nonExisting_shouldThrow() {
        Collection parent = Collection.create("Parent");

        assertThrows(IllegalArgumentException.class,
                () -> parent.removeChildCollection("nonexistent"));
    }
}
