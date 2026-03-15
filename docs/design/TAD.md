# Technical Architecture Document (TAD)

## Project

**SQL Insight — Spring Boot Query Debugger**

Purpose:  
Provide **query observability for Spring Boot applications** with a **developer dashboard** and **annotation-based tracking**.

---

# 1. System Architecture Overview

### Main Flow

```
Application Query Execution
        ↓
Hibernate Interceptor
        ↓
Query Collector
        ↓
Query Analyzer (checks for N+1 patterns)
        ↓
Metrics Engine (deduplicates N+1 pattern warnings)
        ↓
Dashboard API
        ↓
React Dashboard
```

---

# 2. Module Architecture

Project must be **modularized** to keep the core reusable.

```
sql-insight
│
├── sql-insight-core
│
├── sql-insight-annotations
│
├── sql-insight-spring-boot-starter
│
├── sql-insight-dashboard
│
├── sql-insight-ui
│
└── examples
```

---

# 3. Module Responsibilities

## sql-insight-core

Core monitoring engine.

Responsibilities:

• Query interception logic  
• Query storage  
• Query analysis  
• Metrics generation

Contains **no Spring dependencies**.

---

## sql-insight-annotations

Defines developer annotations.

Includes:

```
@QueryTrace
@QueryLabel
@SlowQueryAlert
@DisableQueryTracking
```

---

## sql-insight-spring-boot-starter

Spring Boot integration.

Responsibilities:

• Register Hibernate interceptor & DataSource proxy
• AOP-based automated JPA repository tracking (@Around "Repository+.*")
• Enable dashboard API  
• Read application properties

---

## sql-insight-dashboard

REST API layer.

Provides endpoints used by UI.

---

## sql-insight-ui

React frontend.

Stack:

- React
    
- Tailwind
    
- Shadcn UI
    
- Lucide icons
    
- Poppins font
    

---

# 4. Data Model

## QueryInfo

Represents a captured query.

```java
public class QueryInfo {

    private String sql;

    private long executionTime;

    private String sourceClass;

    private String sourceMethod;

    private String label;

    private LocalDateTime executedAt;

    private boolean slowQuery;

    private boolean nPlusOneDetected;

}
```

---

## QueryMetrics

Aggregated statistics.

```java
public class QueryMetrics {

    private long totalQueries;

    private double avgExecutionTime;

    private long slowQueries;

    private long nPlusOneWarnings;

}
```

---

# 5. Core Engine Components

## QueryInterceptor

Intercept SQL queries using Hibernate.

Preferred approach:

```
Hibernate StatementInspector
```

Implementation:

```java
public class SqlInsightInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {

        QueryContext.start(sql);

        return sql;
    }
}
```

---

## QueryExecutionTimer

Tracks execution time.

```
startTime
endTime
executionDuration
```

Example:

```java
long start = System.nanoTime();
long end = System.nanoTime();
long executionTime = (end - start)/1000000;
```

---

# 6. Query Context Tracker

Tracks which method triggered the query.

Implementation:

```java
StackTraceElement[] stackTrace =
Thread.currentThread().getStackTrace();
```

Filter stack trace:

```
repository
service
controller
```

Extract:

```
sourceClass
sourceMethod
```

---

# 7. Query Collector

Responsible for storing query events.

Use:

```
ConcurrentLinkedQueue
```

Implementation:

```java
public class QueryCollector {

    private static Queue<QueryInfo> queries =
        new ConcurrentLinkedQueue<>();

}
```

Limit memory usage.

```
maxQueries = 1000
```

Old queries removed automatically.

---

# 8. Query Analyzer

Analyzes collected queries.

Responsibilities:

• slow query detection  
• repeated query detection  
• query frequency

---

## Slow Query Detection

Configuration:

```
sql-insight.slow-query-threshold=200
```

Algorithm:

```
if executionTime > threshold
 mark slowQuery
```

---

## N+1 Detection

Detect repeated query patterns.

Example:

```
SELECT * FROM orders WHERE user_id=?
```

Normalize query:

```
remove parameters
generate hash
```

Track frequency.

If repeated:

```
count > threshold
trigger N+1 warning
```

---

# 9. Annotation System

Annotations enable **method-level insights**.

---

## @QueryTrace

Tracks queries inside a method.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryTrace {

    String value() default "";

}
```

Aspect implementation:

```java
@Around("@annotation(QueryTrace) || execution(* org.springframework.data.repository.Repository+.*(..))")
public Object trackQuery(ProceedingJoinPoint joinPoint) {
    QueryContext.startMethod(joinPoint);
    try {
        return joinPoint.proceed();
    } finally {
        QueryContext.endMethod();
    }
}
```

---

## @QueryLabel

Adds label to queries.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryLabel {

    String value();

}
```

Example:

```
@QueryLabel("User Fetch")
```

---

## @SlowQueryAlert

Custom slow query threshold.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlowQueryAlert {

    int threshold() default 200;

}
```

---

## @DisableQueryTracking

Disable monitoring.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableQueryTracking {

}
```

---

# 10. Spring Boot Auto Configuration

Auto register components.

```
SqlInsightAutoConfiguration
```

Implementation:

```java
@Configuration
@ConditionalOnProperty(
 name="sql-insight.enabled",
 havingValue="true",
 matchIfMissing=true
)
public class SqlInsightAutoConfiguration {

    @Bean
    public SqlInsightInspector sqlInsightInspector() {
        return new SqlInsightInspector();
    }

}
```

---

# 11. Configuration Properties

```
SqlInsightProperties
```

Example:

```java
@ConfigurationProperties(prefix="sql-insight")
public class SqlInsightProperties {

    private boolean enabled = true;

    private boolean dashboardEnabled = true;

    private int slowQueryThreshold = 200;

    private int maxQueries = 1000;

}
```

---

# 12. REST API Design

Base path:

```
/sql-insight
```

Endpoints:

### Metrics

```
GET /sql-insight/metrics
```

Returns:

```
totalQueries
avgExecutionTime
slowQueries
nPlusOneWarnings
```

---

### Queries

```
GET /sql-insight/queries
```

Returns recent queries.

---

### Slow Queries

```
GET /sql-insight/slow-queries
```

---

### N+1 Queries

```
GET /sql-insight/nplus1
```

---

# 13. Dashboard Architecture

Stack:

```
React
Tailwind
Shadcn UI
Lucide Icons
Poppins Font
```

---

# 14. UI Page Structure

### Overview Page

Cards:

```
Total Queries
Average Time
Slow Queries
N+1 Alerts
```

Charts:

```
Queries per second
Execution time distribution
```

---

### Queries Page

Table:

```
SQL
Execution Time
Source Method
Timestamp
```

Search functionality required.

---

### Slow Queries Page

Shows queries above threshold.

---

### N+1 Detection Page

Shows repeated query patterns.

---

# 15. Security

Dashboard must not expose sensitive queries.

Controls:

```
sql-insight.dashboard-enabled=false
```

Production recommendation:

```
spring.profiles.active=prod
```

Disable dashboard automatically.

---

# 16. Performance Strategy

Library must have minimal overhead.

Optimization techniques:

• asynchronous processing  
• non-blocking collectors  
• memory capped queues

---

# 17. Build System

Use:

```
Maven multi-module project
```

Modules:

```
core
annotations
starter
dashboard
ui
```

---

# 18. Logging Strategy

Logs only in debug mode.

Example:

```
logging.level.sqlinsight=DEBUG
```

---

# 19. CI/CD

Required pipelines:

```
GitHub Actions
```

Tasks:

```
build
test
lint
publish to Maven Central
```

---

# 20. Documentation

Documentation must include:

• installation guide  
• configuration guide  
• annotation usage  
• dashboard usage  
• troubleshooting

---

# 21. Example Usage

Developer installs dependency.

```
<dependency>
 sql-insight-spring-boot-starter
</dependency>
```

Configuration:

```yaml
sql-insight:
 enabled: true
 dashboard-enabled: true
 slow-query-threshold: 200
```

Access dashboard:

```
http://localhost:8080/sql-insight
```

---

# 22. Future Enhancements

Potential advanced features:

• Query plan analysis  
• Index recommendation engine  
• Query replay  
• distributed tracing integration  
• Grafana plugin