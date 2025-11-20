package com.springbasic.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Real-world example: Database Connection Pool Lifecycle
 *
 * Demonstrates practical use of lifecycle methods for resource management:
 * - @PostConstruct: Initialize connection pool
 * - @PreDestroy: Close connections and cleanup
 *
 * @author Spring Basic Tutorial
 */
@Component
@Getter
public class DatabaseConnectionBean {

    @Value("${app.database.url:jdbc:postgresql://localhost:5432/mydb}")
    private String databaseUrl;

    @Value("${app.database.max-connections:10}")
    private int maxConnections;

    private final List<String> connections = new ArrayList<>();
    private boolean isConnected = false;
    private LocalDateTime connectedAt;
    private LocalDateTime disconnectedAt;
    private int activeConnections = 0;

    /**
     * Initialize database connection pool when bean is created
     */
    @PostConstruct
    public void initializeConnectionPool() {
        System.out.println("DatabaseConnectionBean: Initializing connection pool...");
        System.out.println("Database URL: " + databaseUrl);
        System.out.println("Max Connections: " + maxConnections);

        // Simulate creating connection pool
        for (int i = 1; i <= maxConnections; i++) {
            String connection = "Connection-" + i + "@" + databaseUrl;
            connections.add(connection);
        }

        isConnected = true;
        connectedAt = LocalDateTime.now();
        System.out.println("DatabaseConnectionBean: Connection pool initialized with " +
                         maxConnections + " connections at " + connectedAt);
    }

    /**
     * Business method: Get a connection from the pool
     */
    public String getConnection() {
        if (!isConnected) {
            throw new IllegalStateException("Connection pool not initialized!");
        }

        if (activeConnections >= maxConnections) {
            return "No available connections (max: " + maxConnections + " reached)";
        }

        activeConnections++;
        String connection = connections.get(activeConnections - 1);
        return "Acquired connection: " + connection + " (Active: " + activeConnections + "/" + maxConnections + ")";
    }

    /**
     * Business method: Release connection back to pool
     */
    public String releaseConnection() {
        if (activeConnections > 0) {
            activeConnections--;
            return "Released connection (Active: " + activeConnections + "/" + maxConnections + ")";
        }
        return "No active connections to release";
    }

    /**
     * Cleanup when application is shutting down
     */
    @PreDestroy
    public void closeConnectionPool() {
        System.out.println("DatabaseConnectionBean: Closing connection pool...");

        // Close all connections
        for (String connection : connections) {
            System.out.println("Closing " + connection);
        }

        connections.clear();
        isConnected = false;
        activeConnections = 0;
        disconnectedAt = LocalDateTime.now();

        System.out.println("DatabaseConnectionBean: All connections closed at " + disconnectedAt);
    }

    public String getPoolInfo() {
        return String.format(
            "ConnectionPool[url=%s, max=%d, active=%d, isConnected=%s, connectedAt=%s]",
            databaseUrl, maxConnections, activeConnections, isConnected, connectedAt
        );
    }
}
