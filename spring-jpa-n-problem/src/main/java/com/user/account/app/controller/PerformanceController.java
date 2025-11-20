package com.user.account.app.controller;

import com.user.account.app.benchmark.PerformanceMetrics;
import com.user.account.app.service.PerformanceBenchmarkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceBenchmarkService benchmarkService;

    public PerformanceController(PerformanceBenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping("/compare")
    public Map<String, Object> comparePerformance(
            @RequestParam(defaultValue = "10") int iterations) {

        System.out.println("\n🚀 Starting performance comparison via API endpoint...\n");

        List<PerformanceMetrics> results = benchmarkService.runAllBenchmarks(iterations);
        benchmarkService.printBenchmarkResults(results);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("iterations", iterations);
        response.put("message", "Performance comparison completed. Check console for detailed results.");

        Map<String, Object> summary = new HashMap<>();
        for (PerformanceMetrics metrics : results) {
            Map<String, Object> metricData = new HashMap<>();
            metricData.put("executionTimeMs", metrics.getExecutionTimeMs());
            metricData.put("queryCount", metrics.getQueryCount());
            summary.put(metrics.getMethod(), metricData);
        }
        response.put("results", summary);

        if (results.size() >= 3) {
            PerformanceMetrics nPlusOne = results.get(0);
            PerformanceMetrics fetchJoin = results.get(1);

            double queryReduction = ((double)(nPlusOne.getQueryCount() - fetchJoin.getQueryCount())
                    / nPlusOne.getQueryCount()) * 100;

            response.put("improvement", String.format(
                    "FETCH JOIN reduces queries by %.1f%% (%d -> %d queries)",
                    queryReduction,
                    nPlusOne.getQueryCount(),
                    fetchJoin.getQueryCount()
            ));
        }

        return response;
    }

    @GetMapping("/n-plus-one")
    public Map<String, Object> benchmarkNPlusOne() {
        PerformanceMetrics metrics = benchmarkService.benchmarkNPlusOne();

        Map<String, Object> response = new HashMap<>();
        response.put("method", metrics.getMethod());
        response.put("executionTimeMs", metrics.getExecutionTimeMs());
        response.put("queryCount", metrics.getQueryCount());
        response.put("warning", "⚠️ This method executes multiple queries (N+1 problem)");

        return response;
    }

    @GetMapping("/fetch-join")
    public Map<String, Object> benchmarkFetchJoin() {
        PerformanceMetrics metrics = benchmarkService.benchmarkFetchJoin();

        Map<String, Object> response = new HashMap<>();
        response.put("method", metrics.getMethod());
        response.put("executionTimeMs", metrics.getExecutionTimeMs());
        response.put("queryCount", metrics.getQueryCount());
        response.put("status", "✅ Optimized - uses single query");

        return response;
    }

    @GetMapping("/entity-graph")
    public Map<String, Object> benchmarkEntityGraph() {
        PerformanceMetrics metrics = benchmarkService.benchmarkEntityGraph();

        Map<String, Object> response = new HashMap<>();
        response.put("method", metrics.getMethod());
        response.put("executionTimeMs", metrics.getExecutionTimeMs());
        response.put("queryCount", metrics.getQueryCount());
        response.put("status", "✅ Optimized - uses single query");

        return response;
    }
}
