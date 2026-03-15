export interface QueryInfo {
  sql: string;
  executionTime: number;
  sourceClass: string;
  sourceMethod: string;
  label?: string;
  executedAt: string;
  slowQuery: boolean;
  nPlusOneDetected: boolean;
}

export interface QueryMetrics {
  totalQueries: number;
  avgExecutionTime: number;
  slowQueries: number;
  nPlusOneWarnings: number;
}

export interface NPlusOnePattern {
  queryPattern: string;
  occurrences: number;
  sourceMethod: string;
}

export interface HealthStatus {
  status: 'UP' | 'DOWN';
}
