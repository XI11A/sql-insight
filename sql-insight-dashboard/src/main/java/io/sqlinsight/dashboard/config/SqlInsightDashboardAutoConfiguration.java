package io.sqlinsight.dashboard.config;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.metrics.MetricsEngine;
import io.sqlinsight.dashboard.controller.SqlInsightController;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnProperty(name = "sql-insight.dashboard-enabled", havingValue = "true", matchIfMissing = true)
public class SqlInsightDashboardAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public SqlInsightController sqlInsightController(MetricsEngine metricsEngine, 
                                                     QueryCollector queryCollector, 
                                                     QueryAnalyzer queryAnalyzer) {
        return new SqlInsightController(metricsEngine, queryCollector, queryAnalyzer);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/sql-insight/**")
                .addResourceLocations("classpath:/META-INF/resources/sql-insight/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/sql-insight").setViewName("forward:/sql-insight/index.html");
        registry.addViewController("/sql-insight/").setViewName("forward:/sql-insight/index.html");
    }

    @Bean
    public WebMvcConfigurer sqlInsightCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/sql-insight/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
