package io.sqlinsight.core.model;

public class QueryMetrics {
    private long totalQueries;
    private double avgExecutionTime;
    private long slowQueries;
    private long nPlusOneWarnings;
    
    public QueryMetrics() {
    }

    public QueryMetrics(long totalQueries, double avgExecutionTime, long slowQueries, long nPlusOneWarnings) {
        this.totalQueries = totalQueries;
        this.avgExecutionTime = avgExecutionTime;
        this.slowQueries = slowQueries;
        this.nPlusOneWarnings = nPlusOneWarnings;
    }

    public long getTotalQueries() {
        return totalQueries;
    }

    public void setTotalQueries(long totalQueries) {
        this.totalQueries = totalQueries;
    }

    public double getAvgExecutionTime() {
        return avgExecutionTime;
    }

    public void setAvgExecutionTime(double avgExecutionTime) {
        this.avgExecutionTime = avgExecutionTime;
    }

    public long getSlowQueries() {
        return slowQueries;
    }

    public void setSlowQueries(long slowQueries) {
        this.slowQueries = slowQueries;
    }

    public long getNPlusOneWarnings() {
        return nPlusOneWarnings;
    }

    public void setNPlusOneWarnings(long nPlusOneWarnings) {
        this.nPlusOneWarnings = nPlusOneWarnings;
    }
}
