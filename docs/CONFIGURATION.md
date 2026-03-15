# Configuration Guide

SQL Insight provides several properties to customize its behavior. All properties are prefixed with `sql-insight`.

## Available Properties

| Property | Description | Default |
|----------|-------------|---------|
| `sql-insight.enabled` | Global switch to enable/disable query tracking. | `true` |
| `sql-insight.dashboard-enabled` | Enable/disable the REST API and UI dashboard. | `true` |
| `sql-insight.slow-query-threshold` | Default threshold in ms to mark a query as slow. | `200` |
| `sql-insight.max-queries` | Maximum number of queries to keep in memory. | `1000` |
| `sql-insight.detect-nplus1` | Enable/disable N+1 query detection. | `true` |

## Example Configurations

### YAML (application.yml)

```yaml
sql-insight:
  enabled: true
  dashboard-enabled: true
  slow-query-threshold: 150
  max-queries: 2000
```

### Properties (application.properties)

```properties
sql-insight.enabled=true
sql-insight.dashboard-enabled=true
sql-insight.slow-query-threshold=150
sql-insight.max-queries=2000
```

## Spring Security Integration

If your application uses Spring Security, you must explicitly permit access to the SQL Insight paths. Add the following to your `SecurityFilterChain` configuration:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/sql-insight/**").permitAll() // Permit Dashboard UI and API
            .anyRequest().authenticated()
        )
        // ... rest of your config
    return http.build();
}
```

## Production Profile Auto-Disable

By default, the dashboard is **automatically disabled** when the `prod` Spring profile is active to ensure security. 
To intentionally enable the dashboard in production, explicitly set `sql-insight.dashboard-enabled=true`.

## Performance Overhead

SQL Insight is optimized for minimal overhead. It uses a `ThreadLocal` context for trace tracking and a `ConcurrentLinkedQueue` for storage. 

As of v1.0, **Spring Data JPA repositories are automatically tracked via efficient AOP pointcuts**, significantly reducing the need for stack trace walking. Traditional source tracking via stack trace analysis is only used as a fallback for custom DAO implementations. 

Query masking and serialization carry a small cost per query; it is recommended to keep `sql-insight.dashboard-enabled` as `false` in production unless debugging is required.
