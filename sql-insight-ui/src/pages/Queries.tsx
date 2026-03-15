import { useEffect, useState } from 'react';
import { 
  Search, 
  Copy, 
  Check,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';
import { format } from 'date-fns';
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
import { cn } from '@/lib/utils';

export default function Queries() {
  const [queries, setQueries] = useState<QueryInfo[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [copiedId, setCopiedId] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        const data = await api.getQueries();
        setQueries(data);
      } catch (err) {
        console.error("Failed to fetch queries:", err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const filteredQueries = queries.filter(q => 
    q.sql.toLowerCase().includes(searchTerm.toLowerCase()) ||
    q.sourceMethod.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return <div className="p-8 text-slate-500 animate-pulse">Loading query logs...</div>;
  }

  return (
    <div className="p-8 space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Recent Queries</h2>
          <p className="text-slate-400">Deep inspection of every SQL execution captured by Hibernate.</p>
        </div>
        
        <div className="relative w-full md:w-96">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 w-4 h-4" />
          <input 
            type="text" 
            placeholder="Search by SQL or method..." 
            className="w-full bg-slate-900 border border-slate-800 rounded-lg py-2 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <Card>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[45%]">SQL Query</TableHead>
              <TableHead>Duration</TableHead>
              <TableHead>Source Context</TableHead>
              <TableHead>Timestamp</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredQueries.length > 0 ? (
              filteredQueries.map((query, idx) => (
                <TableRow key={idx}>
                  <TableCell className="font-mono text-[13px] max-w-md truncate group relative">
                    <span className="text-slate-300">{query.sql}</span>
                    <div className="hidden group-hover:block absolute z-20 bg-slate-800 border border-slate-700 p-2 rounded shadow-2xl -top-2 left-0 max-w-lg transition-opacity duration-200">
                       <code className="text-xs text-primary whitespace-pre-wrap">{query.sql}</code>
                    </div>
                  </TableCell>
                  <TableCell>
                    <span className={cn(
                      "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium",
                      query.executionTime > 200 
                        ? "bg-rose-500/10 text-rose-500" 
                        : "bg-emerald-500/10 text-emerald-500"
                    )}>
                      {query.executionTime}ms
                    </span>
                  </TableCell>
                  <TableCell>
                    <div className="flex flex-col">
                      <span className="text-slate-200 font-medium">{query.sourceMethod}</span>
                      <span className="text-xs text-slate-500">{query.sourceClass}</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-slate-400 text-xs">
                    {format(new Date(query.executedAt), 'HH:mm:ss.SSS')}
                  </TableCell>
                  <TableCell className="text-right">
                    <button 
                      onClick={() => copyToClipboard(query.sql, `copy-${idx}`)}
                      className="p-2 hover:bg-slate-700 rounded-lg transition-colors text-slate-400 hover:text-white"
                    >
                      {copiedId === `copy-${idx}` ? <Check size={16} className="text-green-500" /> : <Copy size={16} />}
                    </button>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="h-32 text-center text-slate-500 italic">
                   No queries found matching your search.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </Card>

      <div className="flex items-center justify-between text-sm text-slate-500">
        <p>Showing {filteredQueries.length} of {queries.length} queries</p>
        <div className="flex gap-2">
           <button className="px-3 py-1 border border-slate-800 rounded disabled:opacity-50"><ChevronLeft size={16}/></button>
           <button className="px-3 py-1 border border-slate-800 rounded disabled:opacity-50"><ChevronRight size={16}/></button>
        </div>
      </div>
    </div>
  );
}
