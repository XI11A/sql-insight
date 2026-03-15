# SQL Insight

[![SQL Insight CI](https://github.com/XI11A/sql-insight/actions/workflows/ci.yml/badge.svg)](https://github.com/XI11A/sql-insight/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**SQL Insight** is a lightweight, zero-configuration SQL monitoring and performance library for Spring Boot. It captures real-time SQL execution data, identifies performance bottlenecks (slow queries), and detects common anti-patterns like N+1 queries—all served through a beautiful, modern dashboard.

## Key Features

- 🔍 **Real-time SQL Interception**: Captures queries, timings, and originating source context.
- ⚡ **Auto-Discovery**: Automatic N+1 and Slow Query detection.
- 🏷️ **Smart Labeling**: Fine-grained query tagging using simple annotations.
- 📊 **Modern Dashboard**: Vercel-inspired dark-themed dashboard built with React & Tailwind.
- 🛡️ **Privacy First**: Sensitive query parameters are automatically masked.
- 📦 **Zero Deps**: The core engine is pure Java with zero external complexity.

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>io.sqlinsight</groupId>
    <artifactId>sql-insight-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Access Dashboard

Navigate to `http://localhost:8080/sql-insight` in your browser.

## Annotations at a Glance

| Annotation | Purpose |
|------------|---------|
| `@QueryTrace` | Group queries by method scope. |
| `@QueryLabel` | Tag queries with a custom name. |
| `@SlowQueryAlert` | Set per-method performance thresholds. |
| `@DisableQueryTracking` | Hide sensitive or high-frequency operations. |

## Documentation

- [Installation Guide](docs/INSTALLATION.md)
- [Configuration Guide](docs/CONFIGURATION.md)
- [Annotation Usage](docs/ANNOTATION_USAGE.md)
- [Dashboard Guide](docs/DASHBOARD_USAGE.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)

### Design & Architecture
- [Architecture Overview](docs/design/ARCHITECTURE.md)
- [Technical Architecture (TAD)](docs/design/TAD.md)
- [Business Requirements (BRD)](docs/design/BRD.md)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

**Nivash Chandran** - [GitHub](https://github.com/XI11A) - nivash@email.com
