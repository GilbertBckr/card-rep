package com.cardrep.application.collection;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.repository.CollectionRepository;

/**
 * Use Case: Creation of Collection.
 * A Collection can be created inside another Collection (including RootCollection)
 * with a unique name within its parent.
 */
public class CreateCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public CreateCollectionUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public Collection execute(String parentCollectionId, String name) {
        Collection parent = collectionRepository.findById(parentCollectionId)
                .orElseThrow(() -> new IllegalArgumentException("Parent collection not found: " + parentCollectionId));

        if (!parent.isCollectionNameUnique(name)) {
            throw new IllegalArgumentException(
                    "A collection with name '" + name + "' already exists in the parent collection");
        }

        Collection newCollection = Collection.create(name);
        collectionRepository.save(newCollection);
        parent.addChildCollection(newCollection);
        collectionRepository.save(parent);

        return newCollection;
    }
}
