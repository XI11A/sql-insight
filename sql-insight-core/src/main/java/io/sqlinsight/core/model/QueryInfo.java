package io.sqlinsight.core.model;

import java.time.LocalDateTime;

public class QueryInfo {
    private String sql;
    private long executionTime;
    private String sourceClass;
    private String sourceMethod;
    private String label;
    private LocalDateTime executedAt;
    private boolean slowQuery;
    private boolean nPlusOneDetected;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public boolean isSlowQuery() {
        return slowQuery;
    }

    public void setSlowQuery(boolean slowQuery) {
        this.slowQuery = slowQuery;
    }

    public boolean isnPlusOneDetected() {
        return nPlusOneDetected;
    }

    public void setnPlusOneDetected(boolean nPlusOneDetected) {
        this.nPlusOneDetected = nPlusOneDetected;
    }
}
