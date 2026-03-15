package io.sqlinsight.core.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryContextTest {

    @AfterEach
    void tearDown() {
        QueryContext.clear();
    }

    @Test
    void testThreadLocalIsolationAndState() throws InterruptedException {
        QueryContext.setLabel("test-label");
        QueryContext.setTrackingDisabled(true);
        QueryContext.setSlowQueryThresholdOverride(500);
        QueryContext.startMethod("TestClass", "testMethod");

        assertEquals("test-label", QueryContext.getCurrentLabel());
        assertTrue(QueryContext.isTrackingDisabled());
        assertEquals(500, QueryContext.getSlowQueryThresholdOverride());
        assertEquals("TestClass", QueryContext.getActiveMethodClass());
        assertEquals("testMethod", QueryContext.getActiveMethodName());

        Thread thread = new Thread(() -> {
            assertNull(QueryContext.getCurrentLabel());
            assertFalse(QueryContext.isTrackingDisabled());
            assertNull(QueryContext.getSlowQueryThresholdOverride());
            assertNull(QueryContext.getActiveMethodClass());
            assertNull(QueryContext.getActiveMethodName());
            
            QueryContext.setLabel("thread-label");
            assertEquals("thread-label", QueryContext.getCurrentLabel());
            QueryContext.clear();
        });

        thread.start();
        thread.join();

        assertEquals("test-label", QueryContext.getCurrentLabel());

        QueryContext.endMethod();
        assertNull(QueryContext.getActiveMethodClass());
        assertNull(QueryContext.getActiveMethodName());
        
        QueryContext.clear();
        assertNull(QueryContext.getCurrentLabel());
    }
}
