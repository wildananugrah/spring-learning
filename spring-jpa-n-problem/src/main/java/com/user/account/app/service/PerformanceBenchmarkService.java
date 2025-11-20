package com.user.account.app.service;

import com.user.account.app.benchmark.PerformanceMetrics;
import com.user.account.app.entity.Author;
import com.user.account.app.repository.AuthorRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceBenchmarkService {

    private final AuthorRepository authorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PerformanceBenchmarkService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    private Statistics getStatistics() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        return sessionFactory.getStatistics();
    }

    @Transactional(readOnly = true)
    public PerformanceMetrics benchmarkNPlusOne() {
        Statistics stats = getStatistics();
        stats.clear();

        long startTime = System.currentTimeMillis();
        long startQueryCount = stats.getPrepareStatementCount();

        List<Author> authors = authorRepository.findAll();
        int bookCount = 0;
        for (Author author : authors) {
            bookCount += author.getBooks().size();
        }

        long endTime = System.currentTimeMillis();
        long endQueryCount = stats.getPrepareStatementCount();

        return new PerformanceMetrics(
                "N+1 Problem (findAll)",
                endTime - startTime,
                endQueryCount - startQueryCount,
                authors.size() + bookCount
        );
    }

    @Transactional(readOnly = true)
    public PerformanceMetrics benchmarkFetchJoin() {
        Statistics stats = getStatistics();
        stats.clear();

        long startTime = System.currentTimeMillis();
        long startQueryCount = stats.getPrepareStatementCount();

        List<Author> authors = authorRepository.findAllWithBooks();
        int bookCount = 0;
        for (Author author : authors) {
            bookCount += author.getBooks().size();
        }

        long endTime = System.currentTimeMillis();
        long endQueryCount = stats.getPrepareStatementCount();

        return new PerformanceMetrics(
                "FETCH JOIN Solution",
                endTime - startTime,
                endQueryCount - startQueryCount,
                authors.size() + bookCount
        );
    }

    @Transactional(readOnly = true)
    public PerformanceMetrics benchmarkEntityGraph() {
        Statistics stats = getStatistics();
        stats.clear();

        long startTime = System.currentTimeMillis();
        long startQueryCount = stats.getPrepareStatementCount();

        List<Author> authors = authorRepository.findAllWithBooksEntityGraph();
        int bookCount = 0;
        for (Author author : authors) {
            bookCount += author.getBooks().size();
        }

        long endTime = System.currentTimeMillis();
        long endQueryCount = stats.getPrepareStatementCount();

        return new PerformanceMetrics(
                "EntityGraph Solution",
                endTime - startTime,
                endQueryCount - startQueryCount,
                authors.size() + bookCount
        );
    }

    public List<PerformanceMetrics> runAllBenchmarks(int iterations) {
        List<PerformanceMetrics> results = new ArrayList<>();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERFORMANCE BENCHMARK COMPARISON");
        System.out.println("Running " + iterations + " iterations for each method...");
        System.out.println("=".repeat(80) + "\n");

        // Warm up
        System.out.println("Warming up JVM...");
        for (int i = 0; i < 3; i++) {
            benchmarkNPlusOne();
            benchmarkFetchJoin();
            benchmarkEntityGraph();
        }
        System.out.println("Warm up complete!\n");

        // Benchmark N+1 Problem
        long totalTime = 0;
        long totalQueries = 0;
        for (int i = 0; i < iterations; i++) {
            PerformanceMetrics metrics = benchmarkNPlusOne();
            totalTime += metrics.getExecutionTimeMs();
            totalQueries += metrics.getQueryCount();
        }
        PerformanceMetrics avgNPlusOne = new PerformanceMetrics(
                "N+1 Problem (Average)",
                totalTime / iterations,
                totalQueries / iterations,
                0
        );
        results.add(avgNPlusOne);

        // Benchmark FETCH JOIN
        totalTime = 0;
        totalQueries = 0;
        for (int i = 0; i < iterations; i++) {
            PerformanceMetrics metrics = benchmarkFetchJoin();
            totalTime += metrics.getExecutionTimeMs();
            totalQueries += metrics.getQueryCount();
        }
        PerformanceMetrics avgFetchJoin = new PerformanceMetrics(
                "FETCH JOIN (Average)",
                totalTime / iterations,
                totalQueries / iterations,
                0
        );
        results.add(avgFetchJoin);

        // Benchmark EntityGraph
        totalTime = 0;
        totalQueries = 0;
        for (int i = 0; i < iterations; i++) {
            PerformanceMetrics metrics = benchmarkEntityGraph();
            totalTime += metrics.getExecutionTimeMs();
            totalQueries += metrics.getQueryCount();
        }
        PerformanceMetrics avgEntityGraph = new PerformanceMetrics(
                "EntityGraph (Average)",
                totalTime / iterations,
                totalQueries / iterations,
                0
        );
        results.add(avgEntityGraph);

        return results;
    }

    public void printBenchmarkResults(List<PerformanceMetrics> results) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RESULTS");
        System.out.println("=".repeat(80));

        for (PerformanceMetrics metrics : results) {
            System.out.println(metrics);
        }

        System.out.println("=".repeat(80));

        // Calculate improvements
        if (results.size() >= 3) {
            PerformanceMetrics nPlusOne = results.get(0);
            PerformanceMetrics fetchJoin = results.get(1);
            PerformanceMetrics entityGraph = results.get(2);

            System.out.println("\nPERFORMANCE IMPROVEMENT:");
            System.out.println("-".repeat(80));

            // Query count improvement
            double fetchJoinQueryReduction = ((double)(nPlusOne.getQueryCount() - fetchJoin.getQueryCount())
                    / nPlusOne.getQueryCount()) * 100;
            double entityGraphQueryReduction = ((double)(nPlusOne.getQueryCount() - entityGraph.getQueryCount())
                    / nPlusOne.getQueryCount()) * 100;

            System.out.printf("FETCH JOIN: %.1f%% fewer queries (%d -> %d queries)%n",
                    fetchJoinQueryReduction,
                    nPlusOne.getQueryCount(),
                    fetchJoin.getQueryCount());

            System.out.printf("EntityGraph: %.1f%% fewer queries (%d -> %d queries)%n",
                    entityGraphQueryReduction,
                    nPlusOne.getQueryCount(),
                    entityGraph.getQueryCount());

            // Time improvement
            if (nPlusOne.getExecutionTimeMs() > 0) {
                double fetchJoinTimeReduction = ((double)(nPlusOne.getExecutionTimeMs() - fetchJoin.getExecutionTimeMs())
                        / nPlusOne.getExecutionTimeMs()) * 100;
                double entityGraphTimeReduction = ((double)(nPlusOne.getExecutionTimeMs() - entityGraph.getExecutionTimeMs())
                        / nPlusOne.getExecutionTimeMs()) * 100;

                System.out.printf("\nFETCH JOIN: %.1f%% faster (%d ms -> %d ms)%n",
                        fetchJoinTimeReduction,
                        nPlusOne.getExecutionTimeMs(),
                        fetchJoin.getExecutionTimeMs());

                System.out.printf("EntityGraph: %.1f%% faster (%d ms -> %d ms)%n",
                        entityGraphTimeReduction,
                        nPlusOne.getExecutionTimeMs(),
                        entityGraph.getExecutionTimeMs());
            }

            System.out.println("-".repeat(80));
            System.out.println("\n✅ WINNER: " + determineBestMethod(results));
        }

        System.out.println("=".repeat(80) + "\n");
    }

    private String determineBestMethod(List<PerformanceMetrics> results) {
        if (results.size() < 3) return "N/A";

        PerformanceMetrics fetchJoin = results.get(1);
        PerformanceMetrics entityGraph = results.get(2);

        if (fetchJoin.getExecutionTimeMs() <= entityGraph.getExecutionTimeMs()) {
            return "FETCH JOIN (faster execution, fewer queries)";
        } else {
            return "EntityGraph (faster execution, fewer queries)";
        }
    }
}
