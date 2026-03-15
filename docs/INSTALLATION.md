# Installation Guide

SQL Insight is designed to be easily integrated into any Spring Boot 3+ application using Hibernate.

## 1. Add Maven Dependency

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>io.sqlinsight</groupId>
    <artifactId>sql-insight-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2. Add Gradle Dependency

Add the following to your `build.gradle`:

```groovy
implementation 'io.sqlinsight:sql-insight-spring-boot-starter:1.0.0-SNAPSHOT'
```

## 3. Requirements

- **Java**: 17 or higher
- **Spring Boot**: 3.0.0 or higher
- **Hibernate**: 6.0 or higher

## 4. Quick Start

By default, SQL Insight is enabled once added to the classpath. You will see the **SQL Insight banner** in the console during startup. You can access the dashboard at:
`http://localhost:8080/sql-insight`

To verify the installation, you can also check the health endpoint:
`GET http://localhost:8080/sql-insight/health`
Response: `{"status": "UP"}`
