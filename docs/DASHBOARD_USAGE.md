# Dashboard Usage Guide

The SQL Insight Dashboard provides a real-time visual interface to monitor and analyze SQL queries.

## Accessing the Dashboard

The dashboard is served at:
`http://localhost:8080/sql-insight`

## Dashboard Pages

### 1. Overview
A health summary of your SQL operations.
- **Top Metrics**: Total Queries, Average Execution Time, Slow Queries Count, N+1 Alerts.
- **Query Trends**: Area chart showing query frequency over time.
- **Time Distribution**: Bar chart showing execution time segments.

### 2. All Queries
A searchable list of all recent queries captured by the interceptor.
- **Search**: Filter by SQL text.
- **Traceability**: See the exact source class and method that triggered the query.
- **Copy SQL**: Button to copy the masked SQL statement directly to your clipboard.

### 3. Slow Queries
A dedicated view for performance bottlenecks.
- Lists queries exceeding the threshold.
- Highlights execution times in red for immediate visibility.

### 4. N+1 Detections
Specialized view for redundant queries.
- Groups similar query patterns.
- Shows total occurrences and the originating method.
- Provides recommendations for optimization (e.g., using `@EntityGraph` or `JOIN FETCH`).

## Deployment Considerations

In production environments, the dashboard is **disabled by default**. 
See the [Configuration Guide](CONFIGURATION.md) to learn how to manage access.
