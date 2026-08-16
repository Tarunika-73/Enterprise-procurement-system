import { useEffect, useState } from 'react';
import { Bar, BarChart, Cell, Legend, Line, LineChart, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { getDashboardAnalytics } from '../../services/analyticsService';
import { unwrapApiData } from '../../utils/employeeHelpers';

const STATUS_COLORS = { APPROVED: '#198754', CLOSED: '#7c6fd6', REJECTED: '#dc3545' };
const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 });
const ChartCard = ({ title, children }) => <section className="analytics-chart-card"><h2 className="analytics-chart-title">{title}</h2>{children}</section>;

const DashboardAnalytics = () => {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => {
    let active = true;
    getDashboardAnalytics().then((response) => { if (active) setData(unwrapApiData(response)); }).catch(() => { if (active) setError('Unable to load analytics.'); });
    return () => { active = false; };
  }, []);
  if (error) return <div className="alert alert-warning mt-4 mb-0" role="alert">{error}</div>;
  if (!data) return <div className="analytics-loading mt-4"><span className="spinner-border spinner-border-sm me-2" />Loading analytics…</div>;
  const status = data.requestStatus || [];
  const trend = data.dailyTrend || [];
  const spending = data.departmentSpending || [];
  const empty = (items) => items.length === 0 ? <div className="analytics-empty">No analytics data available.</div> : null;
  return <div className="analytics-dashboard">
    <ChartCard title="Purchase Request Status Distribution">{empty(status) || <ResponsiveContainer width="100%" height={260}><PieChart><Pie data={status} dataKey="count" nameKey="status" innerRadius={58} outerRadius={92} paddingAngle={2}>{status.map((entry) => <Cell key={entry.status} fill={STATUS_COLORS[entry.status] || '#6eb5ff'} />)}</Pie><Tooltip formatter={(value) => [value, 'Requests']} /><Legend /></PieChart></ResponsiveContainer>}</ChartCard>
    <ChartCard title="Daily Procurement Trend">{empty(trend) || <ResponsiveContainer width="100%" height={260}><LineChart data={trend} margin={{ top: 12, right: 12, left: -22, bottom: 0 }}><XAxis dataKey="date" tick={{ fontSize: 12 }} /><YAxis allowDecimals={false} tick={{ fontSize: 12 }} /><Tooltip labelFormatter={(label) => `Date: ${label}`} formatter={(value) => [value, 'Requests']} /><Line type="monotone" dataKey="count" name="Requests" stroke="#7c6fd6" strokeWidth={3} dot={{ r: 4 }} activeDot={{ r: 6 }} /></LineChart></ResponsiveContainer>}</ChartCard>
    <ChartCard title="Spending by Department">{empty(spending) || <ResponsiveContainer width="100%" height={260}><BarChart data={spending} layout="vertical" margin={{ top: 12, right: 20, left: 24, bottom: 0 }}><XAxis type="number" tickFormatter={(value) => currency.format(value)} tick={{ fontSize: 12 }} /><YAxis type="category" dataKey="department" width={104} tick={{ fontSize: 12 }} /><Tooltip formatter={(value) => [currency.format(value), 'Spending']} /><Bar dataKey="amount" name="Spending" fill="#6eb5ff" radius={[0, 6, 6, 0]} /></BarChart></ResponsiveContainer>}</ChartCard>
  </div>;
};

export default DashboardAnalytics;
