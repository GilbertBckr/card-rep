package com.cardrep.adapter.tui;

import com.cardrep.adapter.tui.port.TerminalUI;
import com.cardrep.application.collection.CreateCollectionUseCase;
import com.cardrep.application.collection.DeleteCollectionUseCase;
import com.cardrep.application.collection.ModifyCollectionUseCase;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.repository.CollectionRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * TUI menu for managing collections (create, modify, delete).
 */
public class TuiCollectionMenu {

    private final TerminalUI terminal;
    private final TuiHelper helper;
    private final CreateCollectionUseCase createCollectionUseCase;
    private final ModifyCollectionUseCase modifyCollectionUseCase;
    private final DeleteCollectionUseCase deleteCollectionUseCase;
    private final CollectionRepository collectionRepository;

    public TuiCollectionMenu(TerminalUI terminal, TuiHelper helper,
                             CreateCollectionUseCase createCollectionUseCase,
                             ModifyCollectionUseCase modifyCollectionUseCase,
                             DeleteCollectionUseCase deleteCollectionUseCase,
                             CollectionRepository collectionRepository) {
        this.terminal = terminal;
        this.helper = helper;
        this.createCollectionUseCase = createCollectionUseCase;
        this.modifyCollectionUseCase = modifyCollectionUseCase;
        this.deleteCollectionUseCase = deleteCollectionUseCase;
        this.collectionRepository = collectionRepository;
    }

    /**
     * Run the collection management menu.
     */
    public void run() {
        boolean running = true;
        while (running) {
            List<String> options = List.of(
                    "Create Collection",
                    "Rename Collection",
                    "Delete Collection",
                    "Back to Main Menu"
            );

            int selected = helper.selectFromList(options, 2, 2, "Collection Management");

            switch (selected) {
                case 0 -> createCollection();
                case 1 -> modifyCollection();
                case 2 -> deleteCollection();
                case 3, -1 -> running = false;
            }
        }
    }

    private void createCollection() {
        // Select parent collection
        String parentId = selectCollection("Select parent collection:");
        if (parentId == null) return;

        // Get name
        String name = helper.readText("Create Collection", "Enter collection name:");
        if (name == null || name.isBlank()) {
            helper.showError("Collection name cannot be empty.");
            return;
        }

        try {
            Collection collection = createCollectionUseCase.execute(parentId, name);
            helper.showMessage("Collection created: " + collection.getName());
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void modifyCollection() {
        // Select non-root collection
        String collectionId = selectNonRootCollection("Select collection to rename:");
        if (collectionId == null) return;

        // Get new name
        String newName = helper.readText("Rename Collection", "Enter new name:");
        if (newName == null || newName.isBlank()) {
            helper.showError("Name cannot be empty.");
            return;
        }

        try {
            modifyCollectionUseCase.execute(collectionId, newName);
            helper.showMessage("Collection renamed successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    private void deleteCollection() {
        Collection root = collectionRepository.getRootCollection();
        if (root.getChildCollections().isEmpty()) {
            helper.showMessage("No collections to delete (root cannot be deleted).");
            return;
        }

        // Select non-root collection
        String collectionId = selectNonRootCollection("Select collection to delete:");
        if (collectionId == null) return;

        // Confirm deletion
        if (!helper.confirm("Delete this collection and all its contents?")) {
            helper.showMessage("Deletion cancelled.");
            return;
        }

        try {
            deleteCollectionUseCase.execute(collectionId, root.getId());
            helper.showMessage("Collection deleted successfully.");
        } catch (IllegalArgumentException e) {
            helper.showError(e.getMessage());
        }
    }

    /**
     * Select any collection (including root).
     */
    public String selectCollection(String title) {
        List<Collection> collections = new ArrayList<>();
        List<String> displayItems = new ArrayList<>();

        Collection root = collectionRepository.getRootCollection();
        collectAllCollections(root, collections, displayItems, 0);

        if (collections.isEmpty()) {
            helper.showMessage("No collections available.");
            return null;
        }

        int selected = helper.selectFromList(displayItems, 2, 2, title);
        if (selected == -1) return null;

        return collections.get(selected).getId();
    }

    /**
     * Select a non-root collection.
     */
    public String selectNonRootCollection(String title) {
        List<Collection> collections = new ArrayList<>();
        List<String> displayItems = new ArrayList<>();

        Collection root = collectionRepository.getRootCollection();
        collectNonRootCollections(root, collections, displayItems, 0);

        if (collections.isEmpty()) {
            helper.showMessage("No collections available (root cannot be selected).");
            return null;
        }

        int selected = helper.selectFromList(displayItems, 2, 2, title);
        if (selected == -1) return null;

        return collections.get(selected).getId();
    }

    private void collectAllCollections(Collection collection, List<Collection> collections,
                                       List<String> displayItems, int depth) {
        String indent = "  ".repeat(depth);
        String prefix = depth == 0 ? "[Root] " : "";
        displayItems.add(indent + prefix + collection.getName());
        collections.add(collection);

        for (Collection child : collection.getChildCollections()) {
            collectAllCollections(child, collections, displayItems, depth + 1);
        }
    }

    private void collectNonRootCollections(Collection collection, List<Collection> collections,
                                           List<String> displayItems, int depth) {
        for (Collection child : collection.getChildCollections()) {
            String indent = "  ".repeat(depth);
            displayItems.add(indent + child.getName());
            collections.add(child);
            collectNonRootCollections(child, collections, displayItems, depth + 1);
        }
    }
}
