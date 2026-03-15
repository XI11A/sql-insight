package io.sqlinsight.dashboard.controller;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.metrics.MetricsEngine;
import io.sqlinsight.core.model.QueryMetrics;
import io.sqlinsight.dashboard.dto.QueryInfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sql-insight/api")
public class SqlInsightController {

    private final MetricsEngine metricsEngine;
    private final QueryCollector queryCollector;
    private final QueryAnalyzer queryAnalyzer;

    public SqlInsightController(MetricsEngine metricsEngine, QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        this.metricsEngine = metricsEngine;
        this.queryCollector = queryCollector;
        this.queryAnalyzer = queryAnalyzer;
    }

    @GetMapping("/metrics")
    public QueryMetrics getMetrics() {
        return metricsEngine.generateMetrics();
    }

    @GetMapping("/queries")
    public List<QueryInfoDto> getQueries() {
        return queryCollector.getQueries().stream()
                .map(QueryInfoDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/slow-queries")
    public List<QueryInfoDto> getSlowQueries() {
        return queryCollector.getSlowQueries().stream()
                .map(QueryInfoDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/nplus1")
    public List<QueryAnalyzer.NPlusOnePattern> getNPlusOne() {
        return queryAnalyzer.getNPlusOnePatterns();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Collections.singletonMap("status", "UP");
    }
}
