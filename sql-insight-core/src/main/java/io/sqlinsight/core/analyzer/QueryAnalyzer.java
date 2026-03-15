package io.sqlinsight.core.analyzer;

import io.sqlinsight.core.context.QueryContext;
import io.sqlinsight.core.model.QueryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(QueryAnalyzer.class);

    private int slowQueryThreshold = 200;
    
    // N+1 Tracking (SourceMethod:NormalizedPattern -> Stats)
    private final Map<String, PatternStats> patternOccurrences = new ConcurrentHashMap<>();
    private int nPlusOneThreshold = 5;

    // Throughput Tracking
    private final List<Long> executedTimestamps = Collections.synchronizedList(new ArrayList<>());

    public QueryAnalyzer() {
    }

    public void setSlowQueryThreshold(int slowQueryThreshold) {
        this.slowQueryThreshold = slowQueryThreshold;
    }

    public void setNPlusOneThreshold(int threshold) {
        this.nPlusOneThreshold = threshold;
    }

    public void analyze(QueryInfo queryInfo) {
        // Slow Query Detection (TASK-012)
        int currentThreshold = slowQueryThreshold;
        Integer override = QueryContext.getSlowQueryThresholdOverride();
        if (override != null) {
            currentThreshold = override;
        }
        
        if (queryInfo.getExecutionTime() > currentThreshold) {
            queryInfo.setSlowQuery(true);
            logger.debug("Slow query detected ({}ms > {}ms): {}", queryInfo.getExecutionTime(), currentThreshold, queryInfo.getSql());
        }

        // Output Timestamps for throughput (TASK-014)
        executedTimestamps.add(System.currentTimeMillis());

        // N+1 Detection (TASK-013)
        if (queryInfo.getSourceMethod() != null) {
            String pattern = normalize(queryInfo.getSql());
            String sourceMethod = queryInfo.getSourceClass() + "." + queryInfo.getSourceMethod();
            String hashKey = sourceMethod + ":" + pattern;

            PatternStats stats = patternOccurrences.computeIfAbsent(hashKey, k -> new PatternStats(pattern, sourceMethod));
            int count = stats.increment();

            if (count > nPlusOneThreshold) {
                queryInfo.setnPlusOneDetected(true);
                logger.warn("N+1 query pattern detected (count={}) in {}: {}", count, sourceMethod, pattern);
            }
        }
    }

    private String normalize(String sql) {
        if (sql == null) return "";
        return sql.replaceAll("\\?", "PARAM").trim();
    }

    public List<NPlusOnePattern> getNPlusOnePatterns() {
        List<NPlusOnePattern> results = new ArrayList<>();
        for (PatternStats stats : patternOccurrences.values()) {
            if (stats.getCount() > nPlusOneThreshold) {
                results.add(new NPlusOnePattern(stats.getPattern(), stats.getCount(), stats.getSourceMethod()));
            }
        }
        return results;
    }

    public double getQueriesPerSecond() {
        long now = System.currentTimeMillis();
        long windowStart = now - 10000; // last 10 seconds window

        synchronized (executedTimestamps) {
            executedTimestamps.removeIf(t -> t < windowStart);
            if (executedTimestamps.isEmpty()) return 0.0;
            return executedTimestamps.size() / 10.0; // average over 10s
        }
    }

    public static class NPlusOnePattern {
        private final String queryPattern;
        private final int occurrences;
        private final String sourceMethod;

        public NPlusOnePattern(String queryPattern, int occurrences, String sourceMethod) {
            this.queryPattern = queryPattern;
            this.occurrences = occurrences;
            this.sourceMethod = sourceMethod;
        }

        public String getQueryPattern() { return queryPattern; }
        public int getOccurrences() { return occurrences; }
        public String getSourceMethod() { return sourceMethod; }
    }

    private static class PatternStats {
        private final String pattern;
        private final String sourceMethod;
        private final AtomicInteger count = new AtomicInteger(0);

        public PatternStats(String pattern, String sourceMethod) {
            this.pattern = pattern;
            this.sourceMethod = sourceMethod;
        }

        public int increment() { return count.incrementAndGet(); }
        public int getCount() { return count.get(); }
        public String getPattern() { return pattern; }
        public String getSourceMethod() { return sourceMethod; }
    }
}
