# Annotation Usage Guide

SQL Insight provides several annotations to enhance query traceability and monitoring per method.

## `@QueryTrace`

Tracks and labels all SQL queries executed within the scope of the annotated method.

```java
@QueryTrace("User Authentication")
public User authenticate(String email, String password) {
    return userRepository.findByEmail(email);
}
```

> [!NOTE]
> All Spring Data JPA repositories are automatically tracked. You only need `@QueryTrace` for custom service methods or DAO implementations not following the standard `Repository` interface.

## `@QueryLabel`

Attaches a specific label to every SQL query executed inside the method.

```java
@QueryLabel("Product Search")
public List<Product> search(String query) {
    return productRepository.findByNameContaining(query);
}
```

## `@SlowQueryAlert`

Overrides the global slow query threshold for the duration of the method.

```java
@SlowQueryAlert(threshold = 50) // Mark queries slower than 50ms as slow
public List<Report> generateQuickReport() {
    return reportRepository.fetchRecent();
}
```

## `@DisableQueryTracking`

Completely disables query interception for the annotated method. Useful for performance-sensitive batch jobs or hiding security-related queries.

```java
@DisableQueryTracking
public void updatePasswords(List<User> users) {
    userRepository.saveAll(users);
}
```

## AOP Implementation

These annotations are powered by Spring AOP. Ensure that the methods are called through a Spring-managed bean proxy for the annotations to take effect.
