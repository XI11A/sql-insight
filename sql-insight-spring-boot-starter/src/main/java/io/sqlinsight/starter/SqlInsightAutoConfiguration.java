package io.sqlinsight.starter;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.interceptor.SqlInsightInspector;
import io.sqlinsight.core.metrics.MetricsEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "sql-insight.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SqlInsightProperties.class)
public class SqlInsightAutoConfiguration {

    @Bean
    public QueryCollector queryCollector(SqlInsightProperties properties) {
        return new QueryCollector(properties.getMaxQueries());
    }

    @Bean
    public QueryAnalyzer queryAnalyzer(QueryCollector queryCollector, SqlInsightProperties properties) {
        QueryAnalyzer analyzer = new QueryAnalyzer(queryCollector);
        analyzer.setSlowQueryThreshold(properties.getSlowQueryThreshold());
        if (!properties.isDetectNplus1()) {
            analyzer.setNPlusOneThreshold(Integer.MAX_VALUE);
        }
        return analyzer;
    }

    @Bean
    public SqlInsightInspector sqlInsightInspector(QueryCollector queryCollector) {
        return new SqlInsightInspector(queryCollector);
    }

    @Bean
    public MetricsEngine metricsEngine(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        return new MetricsEngine(queryCollector, queryAnalyzer);
    }
    
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(SqlInsightInspector inspector) {
        return properties -> properties.put("hibernate.session_factory.statement_inspector", inspector);
    }
    
    @Bean
    public io.sqlinsight.starter.aspect.SqlInsightAspect sqlInsightAspect() {
        return new io.sqlinsight.starter.aspect.SqlInsightAspect();
    }
}
