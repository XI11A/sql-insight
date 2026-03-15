package io.sqlinsight.core.metrics;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.model.QueryInfo;
import io.sqlinsight.core.model.QueryMetrics;

import java.util.List;

public class MetricsEngine {

    private final QueryCollector queryCollector;
    private final QueryAnalyzer queryAnalyzer;

    public MetricsEngine(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        this.queryCollector = queryCollector;
        this.queryAnalyzer = queryAnalyzer;
    }

    public QueryMetrics generateMetrics() {
        List<QueryInfo> queries = queryCollector.getQueries();
        long totalQueries = queryCollector.getTotalSessionQueries();
        
        long slowQueries = queries.stream().filter(QueryInfo::isSlowQuery).count();
        long nPlusOneWarnings = queryAnalyzer.getNPlusOnePatterns().size();
        
        double avgExecutionTime = 0.0;
        if (totalQueries > 0) {
            long totalTime = queries.stream().mapToLong(QueryInfo::getExecutionTime).sum();
            avgExecutionTime = (double) totalTime / totalQueries;
        }

        return new QueryMetrics(totalQueries, avgExecutionTime, slowQueries, nPlusOneWarnings);
    }
}
