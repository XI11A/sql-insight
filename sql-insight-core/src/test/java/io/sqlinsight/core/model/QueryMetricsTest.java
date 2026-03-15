package io.sqlinsight.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueryMetricsTest {

    @Test
    void testQueryMetricsConstructorAndFields() {
        QueryMetrics metrics = new QueryMetrics(100L, 15.5, 5L, 2L);

        assertEquals(100L, metrics.getTotalQueries());
        assertEquals(15.5, metrics.getAvgExecutionTime());
        assertEquals(5L, metrics.getSlowQueries());
        assertEquals(2L, metrics.getNPlusOneWarnings());

        metrics.setTotalQueries(200L);
        assertEquals(200L, metrics.getTotalQueries());

        QueryMetrics emptyMetrics = new QueryMetrics();
        emptyMetrics.setSlowQueries(10L);
        assertEquals(10L, emptyMetrics.getSlowQueries());
    }
}
