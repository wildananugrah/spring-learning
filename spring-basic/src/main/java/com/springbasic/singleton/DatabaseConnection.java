package com.springbasic.singleton;

/**
 * Traditional Singleton Pattern Implementation
 *
 * This class demonstrates the classic singleton pattern where only one instance
 * of the class can exist throughout the application lifecycle.
 *
 * Key characteristics:
 * - Private constructor to prevent instantiation
 * - Static instance variable
 * - Thread-safe lazy initialization using double-checked locking
 *
 * @author Spring Basic Tutorial
 */
public class DatabaseConnection {

    // Volatile keyword ensures visibility of changes across threads
    private static volatile DatabaseConnection instance;
    private String connectionString;
    private int connectionCount = 0;

    /**
     * Private constructor to prevent external instantiation
     */
    private DatabaseConnection() {
        this.connectionString = "jdbc:mysql://localhost:3306/mydb";
        System.out.println("DatabaseConnection instance created");
    }

    /**
     * Thread-safe singleton instance getter using double-checked locking
     *
     * @return the single instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Simulates establishing a database connection
     *
     * @return connection message
     */
    public String connect() {
        connectionCount++;
        return "Connected to: " + connectionString + " (Connection #" + connectionCount + ")";
    }

    /**
     * Gets the current connection count
     *
     * @return number of times connect() was called
     */
    public int getConnectionCount() {
        return connectionCount;
    }

    /**
     * Gets the connection string
     *
     * @return the database connection string
     */
    public String getConnectionString() {
        return connectionString;
    }
}
