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
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

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

  const filteredQueries = queries
    .filter(q => 
      q.sql.toLowerCase().includes(searchTerm.toLowerCase()) ||
      q.sourceMethod.toLowerCase().includes(searchTerm.toLowerCase())
    )
    .sort((a, b) => {
      // Latest first
      return new Date(b.executedAt).getTime() - new Date(a.executedAt).getTime();
    });

  // Pagination logic
  const totalPages = Math.ceil(filteredQueries.length / pageSize);
  const startIndex = (currentPage - 1) * pageSize;
  const paginatedQueries = filteredQueries.slice(startIndex, startIndex + pageSize);

  useEffect(() => {
    // Reset to first page when search term changes
    setCurrentPage(1);
  }, [searchTerm]);

  if (loading) {
    return <div className="p-8 text-slate-500 animate-pulse">Loading query logs...</div>;
  }

  const formatDate = (dateStr: string | undefined) => {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      return isNaN(d.getTime()) ? 'N/A' : format(d, 'HH:mm:ss.SSS');
    } catch (e) {
      return 'N/A';
    }
  };

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
            {paginatedQueries.length > 0 ? (
              paginatedQueries.map((query, idx) => (
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
                      query.slowQuery 
                        ? "bg-rose-500/10 text-rose-500" 
                        : "bg-emerald-500/10 text-emerald-500"
                    )}>
                      {query.executionTime}ms
                    </span>
                  </TableCell>
                  <TableCell>
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center gap-2">
                        <span className="text-slate-200 font-semibold">{query.sourceMethod || 'N/A'}</span>
                        {query.sourceClass && (
                          <span className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 text-[10px] text-slate-400 font-mono uppercase tracking-wider">
                            {query.sourceClass.split('.').pop()}
                          </span>
                        )}
                      </div>
                      <span className="text-[11px] text-slate-500 font-mono truncate max-w-[200px]" title={query.sourceClass}>
                        {query.sourceClass}
                      </span>
                    </div>
                  </TableCell>
                  <TableCell className="text-slate-400 text-xs">
                    {formatDate(query.executedAt)}
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
        <p>
          Showing {startIndex + 1} to {Math.min(startIndex + pageSize, filteredQueries.length)} of {filteredQueries.length} results
        </p>
        <div className="flex items-center gap-4">
          <p>Page {currentPage} of {totalPages || 1}</p>
          <div className="flex gap-2">
            <button 
              onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
              disabled={currentPage === 1}
              className="px-3 py-1 border border-slate-800 rounded hover:bg-slate-800 disabled:opacity-30 disabled:hover:bg-transparent transition-colors"
            >
              <ChevronLeft size={16}/>
            </button>
            <button 
              onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
              disabled={currentPage === totalPages || totalPages === 0}
              className="px-3 py-1 border border-slate-800 rounded hover:bg-slate-800 disabled:opacity-30 disabled:hover:bg-transparent transition-colors"
            >
              <ChevronRight size={16}/>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
