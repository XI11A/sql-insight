import { useEffect, useState } from 'react';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer, 
  AreaChart, 
  Area 
} from 'recharts';
import { 
  Activity, 
  Clock, 
  AlertTriangle, 
  ShieldAlert,
  ArrowUpRight 
} from 'lucide-react';
import { api } from '@/services/api';
import type { QueryMetrics, QueryInfo } from '@/types/models';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { cn } from '@/lib/utils';

interface StatItemProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  description: string;
  trend?: string;
  variant?: 'default' | 'warning' | 'error' | 'success';
}

function StatCard({ title, value, icon, description, variant = 'default' }: StatItemProps) {
  const variants = {
    default: "text-primary bg-primary/10",
    warning: "text-amber-500 bg-amber-500/10",
    error: "text-rose-500 bg-rose-500/10",
    success: "text-emerald-500 bg-emerald-500/10",
  };

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center justify-between mb-4">
          <div className={cn("p-2 rounded-lg", variants[variant])}>
            {icon}
          </div>
          <ArrowUpRight className="text-slate-600 w-4 h-4" />
        </div>
        <div>
          <p className="text-sm font-medium text-slate-400 mb-1">{title}</p>
          <h3 className="text-3xl font-bold text-slate-100 tracking-tight">{value}</h3>
          <p className="text-xs text-slate-500 mt-2 flex items-center gap-1">
            {description}
          </p>
        </div>
      </CardContent>
    </Card>
  );
}

export default function Overview() {
  const [metrics, setMetrics] = useState<QueryMetrics | null>(null);
  const [queries, setQueries] = useState<QueryInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const [m, q] = await Promise.all([
          api.getMetrics(),
          api.getQueries()
        ]);
        setMetrics(m);
        setQueries(q);
      } catch (err) {
        console.error("Failed to fetch dashboard data:", err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) {
    return <div className="p-8 text-slate-500 animate-pulse">Loading dashboard metrics...</div>;
  }

  // Sample data for charts
  const timeData = queries.slice(0, 10).map((q, i) => ({
    name: `Q${i+1}`,
    time: q.executionTime,
  })).reverse();

  return (
    <div className="p-8 space-y-8 animate-in fade-in duration-500">
      <div className="flex flex-col gap-1">
        <h2 className="text-3xl font-bold tracking-tight">System Overview</h2>
        <p className="text-slate-400">Real-time performance metrics and query analysis summary.</p>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard 
          title="Total Queries" 
          value={metrics?.totalQueries || 0} 
          icon={<Activity size={20} />} 
          description="Captured in current session"
        />
        <StatCard 
          title="Avg. Execution" 
          value={`${metrics?.avgExecutionTime.toFixed(1)}ms`} 
          icon={<Clock size={20} />} 
          description="Across all captured queries"
          variant={metrics && metrics.avgExecutionTime > 100 ? 'warning' : 'success'}
        />
        <StatCard 
          title="Slow Queries" 
          value={metrics?.slowQueries || 0} 
          icon={<AlertTriangle size={20} />} 
          description="Above execution threshold"
          variant={(metrics?.slowQueries || 0) > 0 ? 'error' : 'default'}
        />
        <StatCard 
          title="N+1 Alerts" 
          value={metrics?.nPlusOneWarnings || 0} 
          icon={<ShieldAlert size={20} />} 
          description="Detected query patterns"
          variant={(metrics?.nPlusOneWarnings || 0) > 0 ? 'error' : 'default'}
        />
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card className="col-span-1">
          <CardHeader>
            <CardTitle>Recent Execution Times</CardTitle>
          </CardHeader>
          <CardContent className="h-[300px] w-full pt-4">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={timeData}>
                <defs>
                  <linearGradient id="colorTime" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366F1" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#6366F1" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(v) => `${v}ms`} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                  itemStyle={{ color: '#6366f1' }}
                />
                <Area type="monotone" dataKey="time" stroke="#6366F1" fillOpacity={1} fill="url(#colorTime)" strokeWidth={2} />
              </AreaChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card className="col-span-1">
          <CardHeader>
            <CardTitle>Query Distribution</CardTitle>
          </CardHeader>
          <CardContent className="h-[300px] w-full pt-4">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={timeData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip 
                  cursor={{ fill: '#334155', opacity: 0.4 }}
                  contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                  itemStyle={{ color: '#22c55e' }}
                />
                <Bar dataKey="time" fill="#22C55E" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
