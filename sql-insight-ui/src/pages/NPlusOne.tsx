import { useEffect, useState } from 'react';
import { 
  Layers, 
  Search, 
  HelpCircle,
  Lightbulb,
  ArrowRight
} from 'lucide-react';
import { api } from '@/services/api';
import type { NPlusOnePattern } from '@/types/models';
import { Card } from '@/components/ui/Card';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from '@/components/ui/Table';

export default function NPlusOne() {
  const [patterns, setPatterns] = useState<NPlusOnePattern[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const data = await api.getNPlusOnePatterns();
        setPatterns(data);
      } catch (err) {
        console.error("Failed to fetch N+1 patterns:", err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) {
    return <div className="p-8 text-slate-500 animate-pulse">Analyzing query patterns for N+1 issues...</div>;
  }

  return (
    <div className="p-8 space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col gap-1">
        <h2 className="text-3xl font-bold tracking-tight flex items-center gap-3">
          <Layers className="text-primary" size={32} />
          N+1 Detections
        </h2>
        <p className="text-slate-400">Identified repeated query patterns that may indicate inefficient relationship loading.</p>
      </div>

      <div className="grid gap-6">
        <Card>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[50%]">Query Pattern (Normalized)</TableHead>
                <TableHead>Occurrences</TableHead>
                <TableHead>Source Method</TableHead>
                <TableHead className="text-right">Recommendation</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {patterns.length > 0 ? (
                patterns.map((pattern, idx) => (
                  <TableRow key={idx}>
                    <TableCell className="font-mono text-[13px] py-6">
                      <div className="bg-slate-900 px-3 py-2 rounded border border-slate-800 text-slate-300">
                        {pattern.queryPattern}
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                         <span className="text-2xl font-bold text-primary">{pattern.occurrences}</span>
                         <span className="text-xs text-slate-500 uppercase font-semibold">Hits</span>
                      </div>
                    </TableCell>
                    <TableCell>
                       <div className="flex flex-col">
                        <span className="text-slate-200 font-medium">{pattern.sourceMethod}</span>
                        <span className="text-xs text-slate-500 italic">Potential trigger point</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-right">
                       <div className="inline-flex items-center gap-2 text-primary font-medium text-sm cursor-help hover:underline">
                          <HelpCircle size={14} />
                          Batch Fetch
                       </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={4} className="h-64 text-center">
                    <div className="flex flex-col items-center justify-center gap-3">
                      <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                         <Layers size={24} />
                      </div>
                      <p className="text-slate-400 font-medium text-lg">No N+1 patterns found.</p>
                      <p className="text-slate-600 text-sm">Your application seems to be using efficient data fetching strategies.</p>
                    </div>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Card>

        <div className="grid md:grid-cols-2 gap-4">
           <div className="bg-primary/5 border border-primary/20 p-6 rounded-xl">
             <div className="flex items-center gap-3 mb-4">
                <Lightbulb className="text-primary" size={20} />
                <h4 className="font-bold text-slate-100">Fixing N+1 with Entity Graphs</h4>
             </div>
             <p className="text-sm text-slate-400 leading-relaxed">
               The most common fix for N+1 is to use <code>@EntityGraph</code> or <code>JOIN FETCH</code> in your JPQL queries. 
               This fetches all required associations in a single SQL statement instead of subsequent lazy-loading calls.
             </p>
             <div className="mt-4 flex items-center gap-2 text-xs font-semibold text-primary uppercase cursor-pointer hover:gap-3 transition-all">
                Learn more <ArrowRight size={14} />
             </div>
           </div>

           <div className="bg-slate-800/20 border border-slate-800 p-6 rounded-xl">
             <div className="flex items-center gap-3 mb-4">
                <Search className="text-primary" size={20} />
                <h4 className="font-bold text-slate-100">Why detect patterns?</h4>
             </div>
             <p className="text-sm text-slate-400 leading-relaxed">
               SQL Insight normalizes queries by stripping parameter values. If the same structure appears many times in a single thread, 
               it's a strong indicator of an iterative fetch in a loop.
             </p>
           </div>
        </div>
      </div>
    </div>
  );
}
