package com.cardrep.plugin.persistence;

import com.cardrep.domain.model.Collection;
import com.cardrep.domain.model.RootCollection;
import com.cardrep.domain.repository.CollectionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of CollectionRepository.
 * Initializes with a RootCollection that always exists.
 */
public class InMemoryCollectionRepository implements CollectionRepository {

    private final Map<String, Collection> collections = new HashMap<>();
    private final RootCollection rootCollection;

    public InMemoryCollectionRepository() {
        this.rootCollection = new RootCollection();
        collections.put(rootCollection.getId(), rootCollection);
    }

    @Override
    public Collection save(Collection collection) {
        collections.put(collection.getId(), collection);
        return collection;
    }

    @Override
    public Optional<Collection> findById(String id) {
        return Optional.ofNullable(collections.get(id));
    }

    @Override
    public void deleteById(String id) {
        if (id.equals(rootCollection.getId())) {
            throw new UnsupportedOperationException("Cannot delete the root collection");
        }
        collections.remove(id);
    }

    @Override
    public Collection getRootCollection() {
        return rootCollection;
    }
}
