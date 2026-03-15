package io.sqlinsight.starter;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.interceptor.SqlInsightInspector;
import io.sqlinsight.core.metrics.MetricsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@AutoConfiguration
@ConditionalOnProperty(name = "sql-insight.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SqlInsightProperties.class)
public class SqlInsightAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SqlInsightAutoConfiguration.class);

    @EventListener(ApplicationReadyEvent.class)
    public void printBanner() {
        String banner = "\n" +
            "\u001B[36m" +
            "************************************************************************\n" +
            "*                                                                      *\n" +
            "*   ____  ___  _      ___           _       _     _                    *\n" +
            "*  / ___|/ _ \\| |    |_ _|_ __  ___(_) __ _| |__ | |_                  *\n" +
            "*  \\___ \\ | | | |     | || '_ \\/ __| |/ _` | '_ \\| __|                 *\n" +
            "*   ___) | |_| | |___ | || | | \\__ \\ | (_| | | | | |_                  *\n" +
            "*  |____/\\__\\_\\|____|___|_| |_|___/_|\\__, |_|_| |_|\\__|                 *\n" +
            "*                                    |___/                             *\n" +
            "*                                                                      *\n" +
            "*  :: SQL Insight ::                         (v1.0.0-SNAPSHOT)         *\n" +
            "*  :: Developed by: Nivash Chandran ::                                 *\n" +
            "*                                                                      *\n" +
            "************************************************************************\n" +
            "\u001B[0m\n";
        
        // Use logger for standard log files
        logger.info("\nSQL Insight library initialized successfully.");
        
        // Force output to console (more visible in dev)
        System.out.println(banner);
        System.out.flush();
    }

    @Bean
    public QueryCollector queryCollector(SqlInsightProperties properties) {
        return new QueryCollector(properties.getMaxQueries());
    }

    @Bean
    public QueryAnalyzer queryAnalyzer(SqlInsightProperties properties) {
        QueryAnalyzer analyzer = new QueryAnalyzer();
        analyzer.setSlowQueryThreshold(properties.getSlowQueryThreshold());
        if (!properties.isDetectNplus1()) {
            analyzer.setNPlusOneThreshold(Integer.MAX_VALUE);
        }
        return analyzer;
    }

    @Bean
    public SqlInsightInspector sqlInsightInspector(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        return new SqlInsightInspector(queryCollector, queryAnalyzer);
    }

    @Bean
    public SqlInsightDataSourcePostProcessor sqlInsightDataSourcePostProcessor(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        return new SqlInsightDataSourcePostProcessor(queryCollector, queryAnalyzer);
    }

    @Bean
    public MetricsEngine metricsEngine(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        return new MetricsEngine(queryCollector, queryAnalyzer);
    }
    
    @Bean
    public io.sqlinsight.starter.aspect.SqlInsightAspect sqlInsightAspect() {
        return new io.sqlinsight.starter.aspect.SqlInsightAspect();
    }
}
