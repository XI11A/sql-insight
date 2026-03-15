# Troubleshooting Guide

Common issues and their solutions for SQL Insight.

## 1. Dashboard Returns 404

**Problem**: Navigating to `/sql-insight` returns a 404 page.

**Solution**:
- Ensure the `sql-insight-dashboard` dependency is included.
- Check if you are in the `prod` profile. If so, the dashboard is disabled by default.
- Verify `sql-insight.dashboard-enabled` is not set to `false` in your properties.
- Check startup logs for `SqlInsightAutoConfiguration` bean creation.

## 2. No Queries Captured

**Problem**: The dashboard is empty even after performing database operations.

**Solution**:
- Ensure `sql-insight.enabled` is `true`.
- Verify you are using Hibernate 6+.
- Check if `hibernate.session_factory.statement_inspector` is being overridden by another custom configuration in your app.
- Check the health endpoint: `GET /sql-insight/health`.

## 3. High Performance Overhead

**Problem**: Application response time significantly increases.

**Solution**:
- SQL Insight performs stack trace walking. If your stack is very deep, this might be slow.
- Reduce `sql-insight.max-queries` to a smaller value (e.g., 200).
- Use `@DisableQueryTracking` on high-throughput batch methods.
- Ensure debug logging is disabled in production.

## 4. REST API Errors

**Problem**: The dashboard UI shows "API Error" or failed to fetch data.

**Solution**:
- Check the browser console and network tab.
- If you are running the UI in dev mode (separated from backend), ensure CORS is allowed. SQL Insight comes with default CORS support for local dev.
- Ensure the backend application is running on the expected port (default 8080).
