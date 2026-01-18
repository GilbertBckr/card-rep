package com.cardrep.domain.repository;

import com.cardrep.domain.model.Collection;

import java.util.Optional;

/**
 * Repository interface for Collection persistence.
 * Defined in the domain layer - implementations reside in infrastructure.
 */
public interface CollectionRepository {

    Collection save(Collection collection);

    Optional<Collection> findById(String id);

    void deleteById(String id);

    /**
     * Returns the root collection (always exists).
     */
    Collection getRootCollection();
}
