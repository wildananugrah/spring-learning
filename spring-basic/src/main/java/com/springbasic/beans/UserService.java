package com.springbasic.beans;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean created using @Component annotation
 *
 * @Component is a generic stereotype for any Spring-managed component.
 * Spring automatically detects @Component classes during classpath scanning
 * and registers them as beans.
 *
 * Component scanning must be enabled (usually via @SpringBootApplication
 * or @ComponentScan on the main application class).
 *
 * Key points:
 * - Auto-detected through component scanning
 * - Default scope is singleton
 * - Bean name defaults to class name with first letter lowercase
 * - Can inject other beans via constructor, field, or setter injection
 *
 * @author Spring Basic Tutorial
 */
@Component
@Data
public class UserService {

    private final Map<Long, User> userDatabase = new HashMap<>();
    private long idCounter = 1;

    /**
     * Constructor - called when Spring creates the bean
     */
    public UserService() {
        System.out.println("UserService component created");
        // Initialize with some sample data
        createUser("John Doe", "john@example.com");
        createUser("Jane Smith", "jane@example.com");
    }

    /**
     * Creates a new user
     *
     * @param name user's name
     * @param email user's email
     * @return created user
     */
    public User createUser(String name, String email) {
        User user = new User(idCounter++, name, email);
        userDatabase.put(user.getId(), user);
        return user;
    }

    /**
     * Finds a user by ID
     *
     * @param id user ID
     * @return user if found, null otherwise
     */
    public User findUserById(Long id) {
        return userDatabase.get(id);
    }

    /**
     * Gets all users
     *
     * @return map of all users
     */
    public Map<Long, User> getAllUsers() {
        return new HashMap<>(userDatabase);
    }

    /**
     * Updates a user
     *
     * @param id user ID
     * @param name new name
     * @param email new email
     * @return updated user or null if not found
     */
    public User updateUser(Long id, String name, String email) {
        User user = userDatabase.get(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
        }
        return user;
    }

    /**
     * Deletes a user
     *
     * @param id user ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long id) {
        return userDatabase.remove(id) != null;
    }

    /**
     * User data class
     */
    @Data
    public static class User {
        private final Long id;
        private String name;
        private String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}
