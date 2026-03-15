import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { 
  LayoutDashboard, 
  List, 
  ZapOff, 
  Layers, 
  Database,
  Activity
} from 'lucide-react';
import { cn } from '@/lib/utils';

interface NavItemProps {
  to: string;
  icon: React.ReactNode;
  label: string;
}

function NavItem({ to, icon, label }: NavItemProps) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        cn(
          "flex items-center gap-3 px-4 py-3 text-sm font-medium transition-colors rounded-lg",
          isActive 
            ? "bg-primary text-white shadow-lg shadow-primary/20" 
            : "text-slate-400 hover:text-white hover:bg-slate-800"
        )
      }
    >
      {icon}
      {label}
    </NavLink>
  );
}

export default function DashboardLayout() {
  return (
    <div className="flex min-h-screen bg-background text-foreground">
      {/* Sidebar */}
      <aside className="w-64 border-r border-slate-800 flex flex-col pt-6 px-4 bg-slate-900/50 backdrop-blur-sm sticky top-0 h-screen">
        <div className="flex items-center gap-2 px-4 mb-10">
          <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center">
            <Database className="text-white w-5 h-5" />
          </div>
          <div>
            <h1 className="font-bold text-lg leading-none">SQL Insight</h1>
            <span className="text-[10px] text-slate-500 font-medium uppercase tracking-wider">Internal Monitoring</span>
          </div>
        </div>

        <nav className="flex flex-col gap-1 flex-1">
          <NavItem to="/" icon={<LayoutDashboard size={20} />} label="Overview" />
          <NavItem to="/queries" icon={<List size={20} />} label="Recent Queries" />
          <NavItem to="/slow-queries" icon={<ZapOff size={20} />} label="Slow Queries" />
          <NavItem to="/nplus1" icon={<Layers size={20} />} label="N+1 Detection" />
        </nav>

        <div className="mt-auto p-4 border-t border-slate-800 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-tight">System Online</span>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-y-auto">
        <header className="h-16 border-b border-slate-800 flex items-center px-8 sticky top-0 bg-background/80 backdrop-blur-md z-10 justify-between">
          <div className="flex items-center gap-2 text-slate-400">
             <Activity size={18} />
             <span className="text-sm font-medium">Hibernate Query Statistics</span>
          </div>
        </header>
        <div className="max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
