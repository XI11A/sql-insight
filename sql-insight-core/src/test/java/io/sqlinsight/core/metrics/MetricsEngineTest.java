package io.sqlinsight.core.metrics;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.model.QueryInfo;
import io.sqlinsight.core.model.QueryMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsEngineTest {

    @Test
    void testGenerateMetrics() {
        QueryCollector collector = new QueryCollector(100);
        QueryAnalyzer analyzer = new QueryAnalyzer(collector);
        MetricsEngine engine = new MetricsEngine(collector, analyzer);

        QueryInfo q1 = new QueryInfo();
        q1.setExecutionTime(100);
        q1.setSlowQuery(false);
        q1.setnPlusOneDetected(false);
        collector.addQuery(q1);

        QueryInfo q2 = new QueryInfo();
        q2.setExecutionTime(300);
        q2.setSlowQuery(true);
        q2.setnPlusOneDetected(true);
        collector.addQuery(q2);

        QueryMetrics metrics = engine.generateMetrics();

        assertEquals(2, metrics.getTotalQueries());
        assertEquals(200.0, metrics.getAvgExecutionTime());
        assertEquals(1, metrics.getSlowQueries());
        assertEquals(1, metrics.getNPlusOneWarnings());
    }
}
