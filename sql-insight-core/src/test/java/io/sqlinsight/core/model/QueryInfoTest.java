package io.sqlinsight.core.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QueryInfoTest {

    @Test
    void testQueryInfoFields() {
        QueryInfo info = new QueryInfo();
        info.setSql("SELECT * FROM users");
        info.setExecutionTime(150L);
        info.setSourceClass("com.example.UserService");
        info.setSourceMethod("getUsers");
        info.setLabel("fetch_users");
        
        LocalDateTime now = LocalDateTime.now();
        info.setExecutedAt(now);
        info.setSlowQuery(true);
        info.setnPlusOneDetected(false);

        assertEquals("SELECT * FROM users", info.getSql());
        assertEquals(150L, info.getExecutionTime());
        assertEquals("com.example.UserService", info.getSourceClass());
        assertEquals("getUsers", info.getSourceMethod());
        assertEquals("fetch_users", info.getLabel());
        assertEquals(now, info.getExecutedAt());
        assertTrue(info.isSlowQuery());
        assertFalse(info.isnPlusOneDetected());
    }
}
