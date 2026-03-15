package io.sqlinsight.core.interceptor;

import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.context.QueryContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlInsightInspectorTest {

    @AfterEach
    void tearDown() {
        QueryContext.clear();
    }

    @Test
    void testMasking() {
        String sql1 = "SELECT * FROM users WHERE email = 'test@example.com'";
        String result1 = QueryMasker.mask(sql1);
        assertEquals("SELECT * FROM users WHERE email = ?", result1);

        String sql2 = "SELECT * FROM users WHERE id = 42";
        String result2 = QueryMasker.mask(sql2);
        assertTrue(result2.contains("?"));
    }

    @Test
    void testInspectAndDisable() {
        QueryCollector collector = new QueryCollector(10);
        SqlInsightInspector inspector = new SqlInsightInspector(collector, null);

        inspector.inspect("SELECT * FROM test");
        assertEquals(1, collector.size());
        assertEquals("SELECT * FROM test", collector.getQueries().get(0).getSql());

        QueryContext.setTrackingDisabled(true);
        inspector.inspect("SELECT * FROM test_skipped");
        assertEquals(1, collector.size()); // should not capture
    }
}
