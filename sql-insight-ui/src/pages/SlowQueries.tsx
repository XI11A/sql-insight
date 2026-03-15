import { useEffect, useState } from 'react';
import { 
  AlertTriangle, 
  Clock, 
  ChevronRight
} from 'lucide-react';
import { api } from '@/services/api';
import type { QueryInfo } from '@/types/models';
import { Card } from '@/components/ui/Card';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from '@/components/ui/Table';

export default function SlowQueries() {
  const [slowQueries, setSlowQueries] = useState<QueryInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const data = await api.getSlowQueries();
        setSlowQueries(data);
      } catch (err) {
        console.error("Failed to fetch slow queries:", err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) {
    return <div className="p-8 text-slate-500 animate-pulse">Analyzing query logs for delays...</div>;
  }

  return (
    <div className="p-8 space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col gap-1">
        <h2 className="text-3xl font-bold tracking-tight flex items-center gap-3">
          <AlertTriangle className="text-amber-500" size={32} />
          Slow Queries
        </h2>
        <p className="text-slate-400">Queries that exceeded the performance threshold (default: 200ms).</p>
      </div>

      <div className="grid gap-6">
        <Card>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[50%]">Slow SQL Statement</TableHead>
                <TableHead>Execution Time</TableHead>
                <TableHead>Source Context</TableHead>
                <TableHead className="text-right">Threshold</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {slowQueries.length > 0 ? (
                slowQueries.map((query, idx) => (
                  <TableRow key={idx}>
                    <TableCell className="font-mono text-[13px] text-rose-400/90 py-6">
                      {query.sql}
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2 font-bold text-lg text-rose-500">
                         <Clock size={16} />
                         {query.executionTime}ms
                      </div>
                    </TableCell>
                    <TableCell>
                       <div className="flex flex-col">
                        <span className="text-slate-200 font-medium">{query.sourceMethod}</span>
                        <span className="text-xs text-slate-500">{query.sourceClass}</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-right text-slate-500 text-xs">
                       200ms
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={4} className="h-64 text-center">
                    <div className="flex flex-col items-center justify-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-emerald-500/10 flex items-center justify-center text-emerald-500">
                         <ChevronRight size={24} />
                      </div>
                      <p className="text-slate-400 font-medium text-lg">Clean sweep! No slow queries detected.</p>
                      <p className="text-slate-600 text-sm">All queries are running within acceptable performance limits.</p>
                    </div>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Card>

        {slowQueries.length > 0 && (
          <div className="bg-amber-500/5 border border-amber-500/20 p-4 rounded-lg flex items-start gap-4">
            <AlertTriangle className="text-amber-500 mt-0.5 shrink-0" size={20} />
            <div>
              <h4 className="font-semibold text-amber-500 text-sm">Optimization Suggestion</h4>
              <p className="text-xs text-amber-500/80 mt-1 leading-relaxed">
                Consider verifying missing indexes on the columns used in <code>WHERE</code> clauses. 
                For complex queries, use <code>EXPLAIN ANALYZE</code> in your database console to find sequential scans.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
