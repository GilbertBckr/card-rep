package com.cardrep.adapter.cli;

import com.cardrep.application.collection.CreateCollectionUseCase;
import com.cardrep.application.collection.DeleteCollectionUseCase;
import com.cardrep.application.collection.ModifyCollectionUseCase;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.repository.CollectionRepository;

import java.util.Scanner;

/**
 * CLI menu for managing collections (create, modify, delete).
 * Uses MenuHelper for shared selection logic (DRY refactoring).
 */
public class CollectionMenu {

    private final Scanner scanner;
    private final CreateCollectionUseCase createCollectionUseCase;
    private final ModifyCollectionUseCase modifyCollectionUseCase;
    private final DeleteCollectionUseCase deleteCollectionUseCase;
    private final CollectionRepository collectionRepository;
    private final MenuHelper menuHelper;

    public CollectionMenu(Scanner scanner, CreateCollectionUseCase createCollectionUseCase,
                          ModifyCollectionUseCase modifyCollectionUseCase,
                          DeleteCollectionUseCase deleteCollectionUseCase,
                          CollectionRepository collectionRepository,
                          MenuHelper menuHelper) {
        this.scanner = scanner;
        this.createCollectionUseCase = createCollectionUseCase;
        this.modifyCollectionUseCase = modifyCollectionUseCase;
        this.deleteCollectionUseCase = deleteCollectionUseCase;
        this.collectionRepository = collectionRepository;
        this.menuHelper = menuHelper;
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Collection Management ---");
            System.out.println("1. Create Collection");
            System.out.println("2. Modify Collection");
            System.out.println("3. Delete Collection");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createCollection();
                case "2" -> modifyCollection();
                case "3" -> deleteCollection();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createCollection() {
        String parentId = menuHelper.selectCollection();
        if (parentId == null) return;

        System.out.print("Enter collection name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Collection name cannot be empty.");
            return;
        }

        try {
            Collection collection = createCollectionUseCase.execute(parentId, name);
            System.out.println("Collection created: " + collection.getName()
                    + " (id: " + collection.getId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void modifyCollection() {
        String collectionId = menuHelper.selectNonRootCollection();
        if (collectionId == null) return;

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine().trim();
        if (newName.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        try {
            modifyCollectionUseCase.execute(collectionId, newName);
            System.out.println("Collection renamed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteCollection() {
        Collection root = collectionRepository.getRootCollection();
        if (root.getChildCollections().isEmpty()) {
            System.out.println("No collections to delete (root cannot be deleted).");
            return;
        }

        String collectionId = menuHelper.selectNonRootCollection();
        if (collectionId == null) return;

        System.out.print("Are you sure? This will delete all content. (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            deleteCollectionUseCase.execute(collectionId, root.getId());
            System.out.println("Collection deleted successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
