package io.sqlinsight.core.collector;

import io.sqlinsight.core.model.QueryInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryCollectorTest {

    @Test
    void testAddAndEvict() {
        QueryCollector collector = new QueryCollector(5);
        for (int i = 0; i < 10; i++) {
            QueryInfo info = new QueryInfo();
            info.setSql("Query " + i);
            collector.addQuery(info);
        }

        assertEquals(5, collector.size());
        assertEquals("Query 5", collector.getQueries().get(0).getSql());
        assertEquals("Query 9", collector.getQueries().get(4).getSql());
    }

    @Test
    void testSlowQueries() {
        QueryCollector collector = new QueryCollector(10);
        QueryInfo fast = new QueryInfo();
        fast.setSlowQuery(false);
        QueryInfo slow = new QueryInfo();
        slow.setSlowQuery(true);

        collector.addQuery(fast);
        collector.addQuery(slow);

        assertEquals(2, collector.size());
        assertEquals(1, collector.getSlowQueries().size());
    }
    
    @Test
    void testClear() {
        QueryCollector collector = new QueryCollector(10);
        collector.addQuery(new QueryInfo());
        assertEquals(1, collector.size());
        collector.clear();
        assertEquals(0, collector.size());
    }
}
