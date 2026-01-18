package com.cardrep.domain.model;

/**
 * The root of the collection tree structure.
 * Cannot be removed and exists from the start.
 * Extends Collection with a fixed ID and name.
 */
public class RootCollection extends Collection {

    private static final String ROOT_ID = "root";
    private static final String ROOT_NAME = "Root";

    public RootCollection() {
        super(ROOT_ID, ROOT_NAME);
    }

    /**
     * The root collection's name cannot be changed.
     */
    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Cannot rename the root collection");
    }
}
