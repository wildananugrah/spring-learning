package com.springbasic.annotations;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Repository Annotation Example
 *
 * @Repository is a specialization of @Component annotation.
 * It indicates that the class is a Data Access Object (DAO) and handles
 * data persistence operations.
 *
 * Key characteristics:
 * - Semantic specialization of @Component
 * - Indicates data access layer (persistence)
 * - Auto-detected through component scanning
 * - Enables automatic exception translation (DataAccessException)
 * - Makes persistence layer exceptions consistent
 *
 * When to use:
 * - Data access layer (DAO)
 * - Classes that interact with database
 * - Classes that handle persistence operations
 *
 * Benefits:
 * - Spring translates persistence exceptions to DataAccessException hierarchy
 * - Makes database-specific exceptions technology-independent
 * - Easier to switch between persistence technologies
 *
 * @author Spring Basic Tutorial
 */
@Repository
public class RepositoryExample {

    // Simulated database using in-memory map
    private final Map<Long, Product> database = new HashMap<>();
    private long idCounter = 1;

    /**
     * Constructor
     */
    public RepositoryExample() {
        System.out.println("RepositoryExample bean created");
        // Initialize with sample data
        save(new Product(null, "Laptop", 999.99));
        save(new Product(null, "Mouse", 29.99));
        save(new Product(null, "Keyboard", 79.99));
    }

    /**
     * Saves a product to the database
     *
     * @param product the product to save
     * @return saved product with generated ID
     */
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter++);
        }
        database.put(product.getId(), product);
        System.out.println("Saved product: " + product);
        return product;
    }

    /**
     * Finds a product by ID
     *
     * @param id product ID
     * @return Optional containing product if found
     */
    public Optional<Product> findById(Long id) {
        System.out.println("Finding product by ID: " + id);
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Finds all products
     *
     * @return map of all products
     */
    public Map<Long, Product> findAll() {
        System.out.println("Finding all products");
        return new HashMap<>(database);
    }

    /**
     * Updates a product
     *
     * @param product product with updated information
     * @return updated product or null if not found
     */
    public Product update(Product product) {
        if (product.getId() != null && database.containsKey(product.getId())) {
            database.put(product.getId(), product);
            System.out.println("Updated product: " + product);
            return product;
        }
        System.out.println("Product not found for update: " + product.getId());
        return null;
    }

    /**
     * Deletes a product by ID
     *
     * @param id product ID
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        boolean deleted = database.remove(id) != null;
        System.out.println("Delete product " + id + ": " + (deleted ? "SUCCESS" : "NOT FOUND"));
        return deleted;
    }

    /**
     * Counts total products
     *
     * @return total number of products
     */
    public long count() {
        return database.size();
    }

    /**
     * Checks if a product exists by ID
     *
     * @param id product ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }

    /**
     * Gets repository information
     *
     * @return repository info
     */
    public String getRepositoryInfo() {
        return String.format(
            "RepositoryExample - Products: %d, HashCode: %d, Annotation: @Repository",
            database.size(),
            this.hashCode()
        );
    }

    /**
     * Product entity class
     */
    @Data
    public static class Product {
        private Long id;
        private String name;
        private double price;

        public Product(Long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
}
