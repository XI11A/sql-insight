package io.sqlinsight.dashboard.dto;

import io.sqlinsight.core.model.QueryInfo;

import java.time.LocalDateTime;

public class QueryInfoDto {
    private final String sql;
    private final long executionTime;
    private final String sourceClass;
    private final String sourceMethod;
    private final LocalDateTime timestamp;

    public QueryInfoDto(QueryInfo info) {
        this.sql = info.getSql();
        this.executionTime = info.getExecutionTime();
        this.sourceClass = info.getSourceClass();
        this.sourceMethod = info.getSourceMethod();
        this.timestamp = info.getExecutedAt();
    }

    public String getSql() { return sql; }
    public long getExecutionTime() { return executionTime; }
    public String getSourceClass() { return sourceClass; }
    public String getSourceMethod() { return sourceMethod; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
