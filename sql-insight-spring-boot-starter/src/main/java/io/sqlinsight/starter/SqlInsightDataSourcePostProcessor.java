package io.sqlinsight.starter;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.interceptor.SqlInsightDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;

public class SqlInsightDataSourcePostProcessor implements BeanPostProcessor {

    private final QueryCollector queryCollector;
    private final QueryAnalyzer queryAnalyzer;

    public SqlInsightDataSourcePostProcessor(QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        this.queryCollector = queryCollector;
        this.queryAnalyzer = queryAnalyzer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && !(bean instanceof SqlInsightDataSource)) {
            return new SqlInsightDataSource((DataSource) bean, queryCollector, queryAnalyzer);
        }
        return bean;
    }
}
