package io.sqlinsight.core.context;

import io.sqlinsight.core.model.QueryInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryContextTrackerTest {

    @Test
    void testPopulateSource() {
        QueryInfo info = new QueryInfo();
        MockService.simulateCall(info);

        assertNotNull(info.getSourceClass());
        assertTrue(info.getSourceClass().contains("MockService"));
        assertEquals("doServiceWork", info.getSourceMethod());
    }

    // Naming it with 'Service' specifically matches the heuristic check in Tracker
    static class MockService {
        static void simulateCall(QueryInfo info) {
            doServiceWork(info);
        }

        static void doServiceWork(QueryInfo info) {
            QueryContextTracker.populateSource(info);
        }
    }
}
