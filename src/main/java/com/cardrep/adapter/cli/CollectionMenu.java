package com.cardrep.adapter.cli;

import com.cardrep.application.collection.CreateCollectionUseCase;
import com.cardrep.application.collection.DeleteCollectionUseCase;
import com.cardrep.application.collection.ModifyCollectionUseCase;
import com.cardrep.domain.model.Collection;
import com.cardrep.domain.repository.CollectionRepository;

import java.util.List;
import java.util.Scanner;

/**
 * CLI menu for managing collections (create, modify, delete).
 */
public class CollectionMenu {

    private final Scanner scanner;
    private final CreateCollectionUseCase createCollectionUseCase;
    private final ModifyCollectionUseCase modifyCollectionUseCase;
    private final DeleteCollectionUseCase deleteCollectionUseCase;
    private final CollectionRepository collectionRepository;

    public CollectionMenu(Scanner scanner, CreateCollectionUseCase createCollectionUseCase,
                          ModifyCollectionUseCase modifyCollectionUseCase,
                          DeleteCollectionUseCase deleteCollectionUseCase,
                          CollectionRepository collectionRepository) {
        this.scanner = scanner;
        this.createCollectionUseCase = createCollectionUseCase;
        this.modifyCollectionUseCase = modifyCollectionUseCase;
        this.deleteCollectionUseCase = deleteCollectionUseCase;
        this.collectionRepository = collectionRepository;
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
        String parentId = selectCollection();
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
        String collectionId = selectNonRootCollection();
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
        List<Collection> children = root.getChildCollections();
        if (children.isEmpty()) {
            System.out.println("No collections to delete (root cannot be deleted).");
            return;
        }

        String collectionId = selectNonRootCollection();
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

    private String selectCollection() {
        Collection root = collectionRepository.getRootCollection();
        System.out.println("\nCollections:");
        System.out.println("  1. " + root.getName() + " (Root)");

        List<Collection> children = root.getChildCollections();
        for (int i = 0; i < children.size(); i++) {
            System.out.println("  " + (i + 2) + ". " + children.get(i).getName());
        }
        System.out.print("Select collection (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index == 0) return root.getId();
            if (index > 0 && index <= children.size()) {
                return children.get(index - 1).getId();
            }
            System.out.println("Invalid selection.");
            return null;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private String selectNonRootCollection() {
        Collection root = collectionRepository.getRootCollection();
        List<Collection> children = root.getChildCollections();
        if (children.isEmpty()) {
            System.out.println("No non-root collections available.");
            return null;
        }

        System.out.println("\nCollections (excluding root):");
        for (int i = 0; i < children.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + children.get(i).getName());
        }
        System.out.print("Select collection (number): ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= children.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return children.get(index).getId();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }
}
