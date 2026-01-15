package com.cardrep.domain.repository;

import com.cardrep.domain.model.Collection;

import java.util.Optional;

public interface CollectionRepository {

    Collection save(Collection collection);

    Optional<Collection> findById(String id);

    void deleteById(String id);

    Collection getRootCollection();
}
