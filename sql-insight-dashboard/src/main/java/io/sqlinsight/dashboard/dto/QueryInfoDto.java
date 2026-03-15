package io.sqlinsight.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sqlinsight.core.model.QueryInfo;

import java.time.LocalDateTime;

public class QueryInfoDto {
    private final String sql;
    private final long executionTime;
    private final String sourceClass;
    private final String sourceMethod;
    private final boolean slowQuery;
    private final boolean nPlusOneDetected;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime executedAt;

    public QueryInfoDto(QueryInfo info) {
        this.sql = info.getSql();
        this.executionTime = info.getExecutionTime();
        this.sourceClass = info.getSourceClass();
        this.sourceMethod = info.getSourceMethod();
        this.slowQuery = info.isSlowQuery();
        this.nPlusOneDetected = info.isnPlusOneDetected();
        this.executedAt = info.getExecutedAt();
    }

    public String getSql() { return sql; }
    public long getExecutionTime() { return executionTime; }
    public String getSourceClass() { return sourceClass; }
    public String getSourceMethod() { return sourceMethod; }
    @JsonProperty("slowQuery")
    public boolean isSlowQuery() { return slowQuery; }

    @JsonProperty("nPlusOneDetected")
    public boolean isnPlusOneDetected() { return nPlusOneDetected; }

    public LocalDateTime getExecutedAt() { return executedAt; }
}
