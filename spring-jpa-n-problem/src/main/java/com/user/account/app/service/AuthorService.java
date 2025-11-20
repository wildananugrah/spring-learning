package com.user.account.app.service;

import com.user.account.app.entity.Author;
import com.user.account.app.entity.Book;
import com.user.account.app.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * This method demonstrates the N+1 query problem.
     *
     * When you call this method:
     * 1. First query: SELECT * FROM authors (1 query)
     * 2. For each author, when accessing author.getBooks():
     *    SELECT * FROM books WHERE author_id = ? (N queries, one for each author)
     *
     * If you have 10 authors, this results in 1 + 10 = 11 queries!
     */
    @Transactional(readOnly = true)
    public void demonstrateNPlusOneProblem() {
        System.out.println("\n========== N+1 PROBLEM DEMONSTRATION ==========");

        // This fetches all authors (1 query)
        List<Author> authors = authorRepository.findAll();

        System.out.println("Fetched " + authors.size() + " authors");

        // When we iterate and access books, it triggers additional queries (N queries)
        for (Author author : authors) {
            System.out.println("Author: " + author.getName() +
                             " has " + author.getBooks().size() + " books");
            // Accessing getBooks() triggers a separate query for EACH author
        }

        System.out.println("===============================================\n");
    }

    /**
     * FIX #1: Using FETCH JOIN
     *
     * This method fixes the N+1 problem by using a fetch join.
     * Only 1 query is executed: SELECT a.*, b.* FROM authors a LEFT JOIN books b ON a.id = b.author_id
     */
    @Transactional(readOnly = true)
    public void demonstrateFetchJoinSolution() {
        System.out.println("\n========== FETCH JOIN SOLUTION ==========");

        // This fetches all authors WITH their books in a single query
        List<Author> authors = authorRepository.findAllWithBooks();

        System.out.println("Fetched " + authors.size() + " authors with books");

        // No additional queries are executed here
        for (Author author : authors) {
            System.out.println("Author: " + author.getName() +
                             " has " + author.getBooks().size() + " books");
        }

        System.out.println("=========================================\n");
    }

    /**
     * FIX #2: Using EntityGraph
     *
     * This method uses @EntityGraph annotation to fetch associations eagerly.
     * Similar to fetch join but uses a different approach.
     */
    @Transactional(readOnly = true)
    public void demonstrateEntityGraphSolution() {
        System.out.println("\n========== ENTITY GRAPH SOLUTION ==========");

        List<Author> authors = authorRepository.findAllWithBooksEntityGraph();

        System.out.println("Fetched " + authors.size() + " authors with books");

        for (Author author : authors) {
            System.out.println("Author: " + author.getName() +
                             " has " + author.getBooks().size() + " books");
        }

        System.out.println("===========================================\n");
    }
}
