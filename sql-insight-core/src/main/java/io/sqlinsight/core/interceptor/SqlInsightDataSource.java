package io.sqlinsight.core.interceptor;

import io.sqlinsight.core.analyzer.QueryAnalyzer;
import io.sqlinsight.core.collector.QueryCollector;
import io.sqlinsight.core.context.QueryContext;
import io.sqlinsight.core.context.QueryContextTracker;
import io.sqlinsight.core.model.QueryInfo;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * A simple DataSource proxy to capture real JDBC execution timing.
 */
public class SqlInsightDataSource implements DataSource {

    private final DataSource delegate;
    private final QueryCollector queryCollector;
    private final QueryAnalyzer queryAnalyzer;

    public SqlInsightDataSource(DataSource delegate, QueryCollector queryCollector, QueryAnalyzer queryAnalyzer) {
        this.delegate = delegate;
        this.queryCollector = queryCollector;
        this.queryAnalyzer = queryAnalyzer;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrapConnection(delegate.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrapConnection(delegate.getConnection(username, password));
    }

    private Connection wrapConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection)
        );
    }

    private class ConnectionHandler implements InvocationHandler {
        private final Connection delegate;

        public ConnectionHandler(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object result = method.invoke(delegate, args);
                if ("prepareStatement".equals(method.getName()) && result instanceof PreparedStatement) {
                    return wrapPreparedStatement((PreparedStatement) result, (String) args[0]);
                }
                if ("createStatement".equals(method.getName()) && result instanceof Statement) {
                    return wrapStatement((Statement) result);
                }
                return result;
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    private PreparedStatement wrapPreparedStatement(PreparedStatement statement, String sql) {
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new StatementHandler(statement, sql)
        );
    }

    private Statement wrapStatement(Statement statement) {
        return (Statement) Proxy.newProxyInstance(
                Statement.class.getClassLoader(),
                new Class[]{Statement.class},
                new StatementHandler(statement, null)
        );
    }

    private class StatementHandler implements InvocationHandler {
        private final Statement delegate;
        private final String sql;

        public StatementHandler(Statement delegate, String sql) {
            this.delegate = delegate;
            this.sql = sql;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                String methodName = method.getName();
                String currentSql = sql;
                
                // Check if it's an execution method
                boolean isExecute = methodName.startsWith("execute");
                if (isExecute && currentSql == null && args != null && args.length > 0 && args[0] instanceof String) {
                    currentSql = (String) args[0];
                }

                if (isExecute && currentSql != null) {
                    if (QueryContext.isTrackingDisabled()) {
                        return method.invoke(delegate, args);
                    }

                    long start = System.currentTimeMillis();
                    try {
                        return method.invoke(delegate, args);
                    } finally {
                        long duration = System.currentTimeMillis() - start;
                        recordQuery(currentSql, duration);
                    }
                }

                return method.invoke(delegate, args);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        private void recordQuery(String sql, long duration) {
            QueryInfo info = new QueryInfo();
            info.setSql(QueryMasker.mask(sql));
            info.setExecutionTime(duration);
            info.setExecutedAt(LocalDateTime.now());
            info.setLabel(QueryContext.getCurrentLabel());

            QueryContextTracker.populateSource(info);
            
            // Fallback for explicitly annotated methods
            if (info.getSourceClass() == null && QueryContext.getActiveMethodClass() != null) {
                info.setSourceClass(QueryContext.getActiveMethodClass());
                info.setSourceMethod(QueryContext.getActiveMethodName());
            }

            if (queryAnalyzer != null) {
                queryAnalyzer.analyze(info);
            }
            queryCollector.addQuery(info);
        }
    }

    // Boilerplate DataSource methods
    @Override public PrintWriter getLogWriter() throws SQLException { return delegate.getLogWriter(); }
    @Override public void setLogWriter(PrintWriter out) throws SQLException { delegate.setLogWriter(out); }
    @Override public void setLoginTimeout(int seconds) throws SQLException { delegate.setLoginTimeout(seconds); }
    @Override public int getLoginTimeout() throws SQLException { return delegate.getLoginTimeout(); }
    @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { return delegate.getParentLogger(); }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException { return delegate.unwrap(iface); }
    @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return delegate.isWrapperFor(iface); }
}
