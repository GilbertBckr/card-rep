package com.cardrep.application.collection;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.repository.CollectionRepository;

public class ModifyCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public ModifyCollectionUseCase(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public Collection execute(String collectionId, String newName) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        collection.setName(newName);
        return collectionRepository.save(collection);
    }
}
