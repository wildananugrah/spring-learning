package com.user.account.app.repository;

import com.user.account.app.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // This causes N+1 problem when accessing books
    List<Author> findAll();

    // Fix for N+1 problem using fetch join
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllWithBooks();

    // Alternative fix using EntityGraph
    @Query("SELECT a FROM Author a")
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"books"})
    List<Author> findAllWithBooksEntityGraph();
}
