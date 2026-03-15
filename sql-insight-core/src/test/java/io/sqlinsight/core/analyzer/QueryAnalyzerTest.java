package io.sqlinsight.core.analyzer;

import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.context.QueryContext;
import io.sqlinsight.core.model.QueryInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryAnalyzerTest {

    private QueryCollector collector;
    private QueryAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        collector = new QueryCollector(100);
        analyzer = new QueryAnalyzer(collector);
        QueryContext.clear();
    }

    @AfterEach
    void tearDown() {
        QueryContext.clear();
    }

    @Test
    void testSlowQueryDetection() {
        QueryInfo info = new QueryInfo();
        info.setSql("SELECT * FROM users");
        info.setExecutionTime(250);
        
        analyzer.analyze(info);
        assertTrue(info.isSlowQuery());
        
        QueryInfo fast = new QueryInfo();
        fast.setSql("SELECT 1");
        fast.setExecutionTime(50);
        analyzer.analyze(fast);
        assertFalse(fast.isSlowQuery());
    }

    @Test
    void testSlowQueryOverride() {
        QueryContext.setSlowQueryThresholdOverride(300);
        
        QueryInfo info = new QueryInfo();
        info.setSql("SELECT * FROM users");
        info.setExecutionTime(250); 
        
        analyzer.analyze(info);
        assertFalse(info.isSlowQuery());
    }

    @Test
    void testNPlusOneDetection() {
        analyzer.setNPlusOneThreshold(3);
        
        for (int i = 0; i < 4; i++) {
            QueryInfo info = new QueryInfo();
            info.setSql("SELECT * FROM orders WHERE user_id = ?"); 
            info.setSourceClass("OrderService");
            info.setSourceMethod("getOrders");
            analyzer.analyze(info);
            
            if (i < 3) {
                assertFalse(info.isnPlusOneDetected());
            } else {
                assertTrue(info.isnPlusOneDetected());
            }
        }
        
        assertEquals(1, analyzer.getNPlusOnePatterns().size());
        assertEquals("OrderService.getOrders", analyzer.getNPlusOnePatterns().get(0).getSourceMethod());
    }
}
