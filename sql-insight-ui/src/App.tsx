import { HashRouter as Router, Routes, Route } from 'react-router-dom';
import DashboardLayout from './layouts/DashboardLayout';
import Overview from './pages/Overview';
import Queries from './pages/Queries';
import SlowQueries from './pages/SlowQueries';
import NPlusOne from './pages/NPlusOne';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<DashboardLayout />}>
          <Route index element={<Overview />} />
          <Route path="queries" element={<Queries />} />
          <Route path="slow-queries" element={<SlowQueries />} />
          <Route path="nplus1" element={<NPlusOne />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
