package com.user.account.app.benchmark;

public class PerformanceMetrics {
    private String method;
    private long executionTimeMs;
    private long queryCount;
    private long entityCount;

    public PerformanceMetrics(String method, long executionTimeMs, long queryCount, long entityCount) {
        this.method = method;
        this.executionTimeMs = executionTimeMs;
        this.queryCount = queryCount;
        this.entityCount = entityCount;
    }

    public String getMethod() {
        return method;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public long getQueryCount() {
        return queryCount;
    }

    public long getEntityCount() {
        return entityCount;
    }

    @Override
    public String toString() {
        return String.format("%-25s | Time: %5d ms | Queries: %3d | Entities: %3d",
                method, executionTimeMs, queryCount, entityCount);
    }
}
