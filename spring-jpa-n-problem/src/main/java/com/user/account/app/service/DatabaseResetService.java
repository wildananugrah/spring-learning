package com.user.account.app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DatabaseResetService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Reset auto-increment sequences for all tables
     * This is useful for testing to ensure predictable IDs
     */
    @Transactional
    public void resetAutoIncrementSequences() {
        System.out.println("\n🔄 Resetting auto-increment sequences...");

        // For PostgreSQL, reset sequences
        // The sequence name format is usually: {table_name}_{column_name}_seq

        // Reset authors sequence
        entityManager.createNativeQuery(
            "ALTER SEQUENCE authors_id_seq RESTART WITH 1"
        ).executeUpdate();

        // Reset books sequence
        entityManager.createNativeQuery(
            "ALTER SEQUENCE books_id_seq RESTART WITH 1"
        ).executeUpdate();

        System.out.println("✅ Auto-increment sequences reset successfully!");
        System.out.println("   - authors_id_seq reset to 1");
        System.out.println("   - books_id_seq reset to 1\n");
    }

    /**
     * Delete all data and reset sequences
     * Complete clean slate for testing
     */
    @Transactional
    public void resetDatabase() {
        System.out.println("\n🔄 Resetting entire database...");

        // Delete all data (order matters due to foreign keys)
        entityManager.createNativeQuery("DELETE FROM books").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM authors").executeUpdate();

        // Reset sequences
        resetAutoIncrementSequences();

        System.out.println("✅ Database reset complete!\n");
    }

    /**
     * Truncate tables and reset sequences (faster than DELETE)
     * Use TRUNCATE for better performance
     */
    @Transactional
    public void truncateAndReset() {
        System.out.println("\n🔄 Truncating tables and resetting sequences...");

        try {
            // TRUNCATE is faster than DELETE but requires CASCADE for foreign keys
            entityManager.createNativeQuery(
                "TRUNCATE TABLE authors, books RESTART IDENTITY CASCADE"
            ).executeUpdate();

            System.out.println("✅ Tables truncated and sequences reset!");
            System.out.println("   - All data removed");
            System.out.println("   - Sequences reset to 1\n");
        } catch (Exception e) {
            System.out.println("⚠️  TRUNCATE failed, falling back to DELETE...");
            resetDatabase();
        }
    }

    /**
     * Get current sequence values
     */
    public void showCurrentSequenceValues() {
        System.out.println("\n📊 Current Sequence Values:");

        // Get authors sequence value
        Long authorsSeq = ((Number) entityManager.createNativeQuery(
            "SELECT last_value FROM authors_id_seq"
        ).getSingleResult()).longValue();

        // Get books sequence value
        Long booksSeq = ((Number) entityManager.createNativeQuery(
            "SELECT last_value FROM books_id_seq"
        ).getSingleResult()).longValue();

        System.out.println("   - authors_id_seq: " + authorsSeq);
        System.out.println("   - books_id_seq: " + booksSeq + "\n");
    }

    /**
     * Reset sequence to specific value
     */
    @Transactional
    public void resetSequenceToValue(String tableName, String columnName, long startValue) {
        String sequenceName = tableName + "_" + columnName + "_seq";

        entityManager.createNativeQuery(
            "ALTER SEQUENCE " + sequenceName + " RESTART WITH " + startValue
        ).executeUpdate();

        System.out.println("✅ Sequence " + sequenceName + " reset to " + startValue);
    }
}
