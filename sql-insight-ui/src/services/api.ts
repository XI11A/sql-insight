import type { QueryInfo, QueryMetrics, NPlusOnePattern, HealthStatus } from '../types/models';

const BASE_URL = import.meta.env.DEV ? 'http://localhost:8080/sql-insight/api' : '/sql-insight/api';

// Helper for dev mocking
const USE_MOCKS = import.meta.env.DEV && true; 

export const api = {
  async getMetrics(): Promise<QueryMetrics> {
    if (USE_MOCKS) {
      return {
        totalQueries: 1240,
        avgExecutionTime: 18.5,
        slowQueries: 12,
        nPlusOneWarnings: 3
      };
    }
    const response = await fetch(`${BASE_URL}/metrics`);
    return response.json();
  },

  async getQueries(): Promise<QueryInfo[]> {
    if (USE_MOCKS) {
      return Array.from({ length: 15 }, (_, i) => ({
        sql: `SELECT * FROM users WHERE id = :id AND status = 'ACTIVE' -- Query ${i}`,
        executionTime: Math.floor(Math.random() * 300),
        sourceClass: 'UserRepository',
        sourceMethod: 'findById',
        label: i % 5 === 0 ? 'User Profile Fetch' : undefined,
        executedAt: new Date(Date.now() - i * 60000).toISOString(),
        slowQuery: false,
        nPlusOneDetected: false
      })).map(q => ({ ...q, slowQuery: q.executionTime > 200 }));
    }
    const response = await fetch(`${BASE_URL}/queries`);
    return response.json();
  },

  async getSlowQueries(): Promise<QueryInfo[]> {
    const queries = await this.getQueries();
    return queries.filter(q => q.slowQuery);
  },

  async getNPlusOnePatterns(): Promise<NPlusOnePattern[]> {
    if (USE_MOCKS) {
      return [
        {
          queryPattern: 'SELECT * FROM orders WHERE user_id = ?',
          occurrences: 50,
          sourceMethod: 'OrderService.getOrdersForUsers'
        },
        {
          queryPattern: 'SELECT * FROM addresses WHERE profile_id = ?',
          occurrences: 12,
          sourceMethod: 'ProfileRepository.findAll'
        }
      ];
    }
    const response = await fetch(`${BASE_URL}/nplus1`);
    return response.json();
  },

  async getHealth(): Promise<HealthStatus> {
    if (USE_MOCKS) return { status: 'UP' };
    const response = await fetch(`${BASE_URL}/health`);
    return response.json();
  }
};
