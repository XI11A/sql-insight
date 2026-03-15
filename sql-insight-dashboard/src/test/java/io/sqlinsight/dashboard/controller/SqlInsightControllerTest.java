package io.sqlinsight.dashboard.controller;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.metrics.MetricsEngine;
import io.sqlinsight.dashboard.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TestApplication.class, SqlInsightControllerTest.Config.class}, properties = "sql-insight.dashboard-enabled=true")
@AutoConfigureMockMvc
class SqlInsightControllerTest {

    @Configuration
    static class Config {
        @Bean
        @Primary
        public QueryCollector queryCollector() {
            return new QueryCollector(100);
        }
        @Bean
        @Primary
        public QueryAnalyzer queryAnalyzer(QueryCollector queryCollector) {
            return new QueryAnalyzer(queryCollector);
        }
        @Bean
        @Primary
        public MetricsEngine metricsEngine(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
            return new MetricsEngine(queryCollector, queryAnalyzer);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/sql-insight/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testQueriesEndpoint() throws Exception {
        mockMvc.perform(get("/sql-insight/api/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
