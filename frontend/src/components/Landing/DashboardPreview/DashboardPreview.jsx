import { useState } from 'react';
import { motion, AnimatePresence, useReducedMotion } from 'framer-motion';
import './DashboardPreview.css';

const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.10, delayChildren: delay } },
});

const TABS = [
  {
    key: 'admin',
    label: 'Admin',
    icon: 'bi-shield-fill',
    colorCls: 'tab-primary',
    title: 'Admin Dashboard',
    subtitle: 'Full system control — users, roles, audit logs, and platform settings.',
    metrics: [
      { icon: 'bi-people-fill',       label: 'Total Users',      value: '248',   badge: '+12 this month', badgeCls: 'b-green'  },
      { icon: 'bi-diagram-3-fill',    label: 'Active Workflows',  value: '34',    badge: '6 pending',      badgeCls: 'b-amber'  },
      { icon: 'bi-shield-check',      label: 'Compliance Score',  value: '98%',   badge: 'Excellent',      badgeCls: 'b-green'  },
      { icon: 'bi-journal-text',      label: 'Audit Events',      value: '1,204', badge: 'Last 30 days',   badgeCls: 'b-purple' },
    ],
    bars: [72, 88, 65, 91, 78, 95, 83],
  },
  {
    key: 'manager',
    label: 'Manager',
    icon: 'bi-person-badge-fill',
    colorCls: 'tab-secondary',
    title: 'Manager Dashboard',
    subtitle: 'Approve requests, monitor team spend, and track department budgets.',
    metrics: [
      { icon: 'bi-file-earmark-text', label: 'Pending Approvals', value: '18',    badge: '3 urgent',       badgeCls: 'b-amber'  },
      { icon: 'bi-bag-check-fill',    label: 'POs This Month',    value: '42',    badge: '↑ 8%',           badgeCls: 'b-green'  },
      { icon: 'bi-piggy-bank-fill',   label: 'Budget Used',       value: '64%',   badge: '$128K remaining', badgeCls: 'b-purple' },
      { icon: 'bi-people-fill',       label: 'Team Members',      value: '24',    badge: '2 on leave',     badgeCls: 'b-blue'   },
    ],
    bars: [55, 70, 82, 60, 88, 74, 91],
  },
  {
    key: 'finance',
    label: 'Finance',
    icon: 'bi-bank2',
    colorCls: 'tab-accent',
    title: 'Finance Dashboard',
    subtitle: 'Invoice matching, payment processing, and spend analytics in one view.',
    metrics: [
      { icon: 'bi-receipt-cutoff',    label: 'Invoices Pending',  value: '23',    badge: '$84K total',     badgeCls: 'b-amber'  },
      { icon: 'bi-credit-card-fill',  label: 'Payments Made',     value: '$2.4M', badge: 'This quarter',   badgeCls: 'b-green'  },
      { icon: 'bi-graph-up-arrow',    label: 'Savings Achieved',  value: '18%',   badge: 'vs last year',   badgeCls: 'b-purple' },
      { icon: 'bi-exclamation-circle','label': 'Discrepancies',   value: '4',     badge: 'Needs review',   badgeCls: 'b-red'    },
    ],
    bars: [60, 75, 90, 68, 85, 72, 94],
  },
  {
    key: 'employee',
    label: 'Employee',
    icon: 'bi-person-fill',
    colorCls: 'tab-green',
    title: 'Employee Dashboard',
    subtitle: 'Raise purchase requests, track approvals, and view order status.',
    metrics: [
      { icon: 'bi-file-earmark-plus', label: 'My Requests',       value: '7',     badge: '2 approved',     badgeCls: 'b-green'  },
      { icon: 'bi-clock-history',     label: 'Avg Approval Time', value: '18h',   badge: '↓ 22% faster',   badgeCls: 'b-green'  },
      { icon: 'bi-bag-check-fill',    label: 'Orders Delivered',  value: '12',    badge: 'This month',     badgeCls: 'b-blue'   },
      { icon: 'bi-bell-fill',         label: 'Notifications',     value: '5',     badge: '2 unread',       badgeCls: 'b-amber'  },
    ],
    bars: [45, 68, 55, 80, 62, 88, 70],
  },
  {
    key: 'vendor',
    label: 'Vendor',
    icon: 'bi-shop',
    colorCls: 'tab-amber',
    title: 'Vendor Dashboard',
    subtitle: 'View purchase orders, submit invoices, and track payment status.',
    metrics: [
      { icon: 'bi-bag-check-fill',    label: 'Active POs',        value: '9',     badge: '3 new today',    badgeCls: 'b-green'  },
      { icon: 'bi-receipt-cutoff',    label: 'Invoices Submitted', value: '14',   badge: '6 paid',         badgeCls: 'b-blue'   },
      { icon: 'bi-star-fill',         label: 'Performance Score', value: '4.7',   badge: 'Top Supplier',   badgeCls: 'b-purple' },
      { icon: 'bi-currency-dollar',   label: 'Payments Received', value: '$340K', badge: 'This quarter',   badgeCls: 'b-green'  },
    ],
    bars: [80, 65, 90, 72, 85, 78, 92],
  },
];

const DashboardPreview = () => {
  const [active, setActive] = useState('admin');
  const tab = TABS.find(t => t.key === active);
  const reduced = useReducedMotion();

  return (
    <section className="dp-section" id="dashboards">
      <div className="dp-orb dp-orb-1" aria-hidden="true" />
      <div className="dp-orb dp-orb-2" aria-hidden="true" />

      <div className="dp-container">

        {/* ── Header ── */}
        <motion.div
          className="dp-header"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.5 }}
          variants={stagger(0)}
        >
          <motion.span className="section-badge" variants={fadeUp}>
            <i className="bi bi-layout-text-sidebar-reverse"></i> Dashboard Previews
          </motion.span>
          <motion.h2 className="dp-title" variants={fadeUp}>
            A View Built for<br />
            <span className="gradient-text">Every Role</span>
          </motion.h2>
          <motion.p className="dp-desc" variants={fadeUp}>
            Each stakeholder gets a tailored dashboard — showing exactly what
            they need, nothing they don't.
          </motion.p>
        </motion.div>

        {/* ── Tab switcher ── */}
        <motion.div
          className="dp-tabs"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.5 }}
          transition={{ duration: 0.55, ease: [0.22, 1, 0.36, 1] }}
        >
          {TABS.map(({ key, label, icon, colorCls }) => (
            <button
              key={key}
              className={`dp-tab ${colorCls}${active === key ? ' is-active' : ''}`}
              onClick={() => setActive(key)}
            >
              <i className={`bi ${icon}`}></i>
              <span>{label}</span>
            </button>
          ))}
        </motion.div>

        {/* ── Mock dashboard window ── */}
        <AnimatePresence mode="wait">
        <motion.div
          key={active}
          className="dp-window"
          initial={{ opacity: 0, y: reduced ? 0 : 24, scale: reduced ? 1 : 0.98 }}
          animate={{ opacity: 1, y: 0,  scale: 1    }}
          exit={{   opacity: 0, y: reduced ? 0 : -16, scale: reduced ? 1 : 0.98 }}
          transition={{ duration: 0.38, ease: [0.22, 1, 0.36, 1] }}
        >
          {/* Window chrome */}
          <div className="dp-chrome">
            <span className="dp-dot red"   />
            <span className="dp-dot amber" />
            <span className="dp-dot green" />
            <span className="dp-chrome-title">{tab.title}</span>
          </div>

          {/* Window body */}
          <div className="dp-body">
            {/* Left sidebar mock */}
            <div className="dp-sidebar">
              <div className="dp-sidebar-logo">
                <i className="bi bi-box-seam-fill"></i>
              </div>
              {['bi-house-fill','bi-file-earmark-text','bi-bag-check-fill',
                'bi-receipt-cutoff','bi-graph-up-arrow','bi-bell-fill',
                'bi-gear-fill'].map((ic, i) => (
                <div key={i} className={`dp-sidebar-item${i === 0 ? ' active' : ''}`}>
                  <i className={`bi ${ic}`}></i>
                </div>
              ))}
            </div>

            {/* Main content */}
            <div className="dp-main">
              {/* Top bar */}
              <div className="dp-topbar">
                <div className="dp-topbar-title">{tab.subtitle}</div>
                <div className="dp-topbar-actions">
                  <div className="dp-search-mock" />
                  <div className="dp-avatar" />
                </div>
              </div>

              {/* Metric cards */}
              <div className="dp-metrics">
                {tab.metrics.map(({ icon, label, value, badge, badgeCls }) => (
                  <div key={label} className="dp-metric">
                    <div className="dp-metric-top">
                      <i className={`bi ${icon} dp-metric-icon`}></i>
                      <span className={`dp-metric-badge ${badgeCls}`}>{badge}</span>
                    </div>
                    <div className="dp-metric-value">{value}</div>
                    <div className="dp-metric-label">{label}</div>
                  </div>
                ))}
              </div>

              {/* Chart row */}
              <div className="dp-chart-row">
                <div className="dp-chart-card">
                  <div className="dp-chart-title">Activity Overview</div>
                  <div className="dp-bars">
                    {tab.bars.map((h, i) => (
                      <motion.div
                        key={i}
                        className="dp-bar"
                        style={{ '--bh': `${h}%` }}
                        initial={{ scaleY: 0 }}
                        animate={{ scaleY: 1 }}
                        transition={{ duration: 0.4, delay: i * 0.06, ease: 'easeOut' }}
                      />
                    ))}
                  </div>
                  <div className="dp-bar-labels">
                    {['Mon','Tue','Wed','Thu','Fri','Sat','Sun'].map(d => (
                      <span key={d}>{d}</span>
                    ))}
                  </div>
                </div>

                <div className="dp-recent-card">
                  <div className="dp-chart-title">Recent Activity</div>
                  {[
                    { icon: 'bi-check-circle-fill', text: 'PO-2024-0142 approved',  time: '2m ago',  cls: 'act-green'  },
                    { icon: 'bi-clock-fill',        text: 'Invoice INV-089 pending', time: '14m ago', cls: 'act-amber'  },
                    { icon: 'bi-truck',             text: 'Delivery confirmed',      time: '1h ago',  cls: 'act-blue'   },
                    { icon: 'bi-bell-fill',         text: 'Budget alert: 80% used',  time: '3h ago',  cls: 'act-red'    },
                  ].map(({ icon, text, time, cls }) => (
                    <div key={text} className="dp-activity-row">
                      <i className={`bi ${icon} ${cls}`}></i>
                      <span className="dp-act-text">{text}</span>
                      <span className="dp-act-time">{time}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </motion.div>
        </AnimatePresence>

      </div>
    </section>
  );
};

export default DashboardPreview;
