import { useEffect, useRef, useState } from 'react';
import { motion, useInView, useReducedMotion } from 'framer-motion';
import './Statistics.css';

/* ── Animation helpers ── */
const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.12, delayChildren: delay } },
});

/* ── Count-up hook ── */
const useCountUp = (target, duration = 1800, shouldStart = false) => {
  const [count, setCount] = useState(0);
  const reduced = useReducedMotion();

  useEffect(() => {
    if (!shouldStart) return;
    if (reduced) { setCount(target); return; }
    let startTime = null;
    const step = (timestamp) => {
      if (!startTime) startTime = timestamp;
      const progress = Math.min((timestamp - startTime) / duration, 1);
      /* ease-out cubic */
      const eased = 1 - Math.pow(1 - progress, 3);
      setCount(Math.floor(eased * target));
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }, [target, duration, shouldStart]);

  return count;
};

/* ── Stat data ── */
const STATS = [
  {
    icon: 'bi-bag-check-fill',
    colorCls: 'stat-primary',
    target: 1000,
    suffix: '+',
    label: 'Purchase Orders',
    sub: 'processed monthly',
  },
  {
    icon: 'bi-shop',
    colorCls: 'stat-secondary',
    target: 500,
    suffix: '+',
    label: 'Suppliers',
    sub: 'onboarded & active',
  },
  {
    icon: 'bi-people-fill',
    colorCls: 'stat-accent',
    target: 250,
    suffix: '+',
    label: 'Employees',
    sub: 'across departments',
  },
  {
    icon: 'bi-activity',
    colorCls: 'stat-green',
    target: 99,
    suffix: '.9%',
    label: 'Availability',
    sub: 'guaranteed uptime SLA',
  },
  {
    icon: 'bi-clock-history',
    colorCls: 'stat-amber',
    target: 60,
    suffix: '%',
    label: 'Faster Approvals',
    sub: 'vs manual processes',
  },
  {
    icon: 'bi-currency-dollar',
    colorCls: 'stat-primary',
    target: 40,
    suffix: '%',
    label: 'Cost Reduction',
    sub: 'in procurement spend',
  },
];

/* ── Individual animated counter card ── */
const StatCard = ({ icon, colorCls, target, suffix, label, sub, index }) => {
  const ref     = useRef(null);
  const inView  = useInView(ref, { once: true, amount: 0.5 });
  const count   = useCountUp(target, 1600 + index * 80, inView);

  return (
    <motion.div
      ref={ref}
      className="stat-card"
      variants={fadeUp}
      whileHover={{ y: -8, scale: 1.03, transition: { duration: 0.25, ease: 'easeOut' } }}
    >
      {/* icon */}
      <div className={`stat-icon ${colorCls}`}>
        <i className={`bi ${icon}`}></i>
      </div>

      {/* counter */}
      <div className="stat-counter">
        <span className="stat-value">{count}</span>
        <span className="stat-suffix">{suffix}</span>
      </div>

      {/* labels */}
      <h4 className="stat-label">{label}</h4>
      <p className="stat-sub">{sub}</p>

      {/* hover glow */}
      <div className="stat-glow" aria-hidden="true" />
    </motion.div>
  );
};

/* ── Main section ── */
const Statistics = () => (
  <section className="statistics-section" id="analytics">
    {/* full-bleed gradient background */}
    <div className="stats-bg" aria-hidden="true" />

    {/* floating orbs inside the gradient */}
    <div className="stats-orb stats-orb-1" aria-hidden="true" />
    <div className="stats-orb stats-orb-2" aria-hidden="true" />
    <div className="stats-orb stats-orb-3" aria-hidden="true" />

    <div className="statistics-container">

      {/* ── Header ── */}
      <motion.div
        className="statistics-header"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.5 }}
        variants={stagger(0)}
      >
        <motion.span className="stats-badge" variants={fadeUp}>
          <i className="bi bi-bar-chart-fill"></i> By The Numbers
        </motion.span>

        <motion.h2 className="statistics-title" variants={fadeUp}>
          Procurement at<br />
          <span className="stats-gradient-text">Enterprise Scale</span>
        </motion.h2>

        <motion.p className="statistics-desc" variants={fadeUp}>
          Real numbers from real deployments — the platform that scales
          with your organisation from day one.
        </motion.p>
      </motion.div>

      {/* ── Counter grid ── */}
      <motion.div
        className="stats-grid"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.15 }}
        variants={stagger(0.06)}
      >
        {STATS.map((stat, i) => (
          <StatCard key={stat.label} {...stat} index={i} />
        ))}
      </motion.div>

      {/* ── Bottom trust bar ── */}
      <motion.div
        className="stats-trust-bar"
        initial={{ opacity: 0, y: 24 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.6 }}
        transition={{ duration: 0.65, ease: [0.22, 1, 0.36, 1] }}
      >
        {[
          { icon: 'bi-shield-fill-check', text: 'SOC 2 Type II Certified'   },
          { icon: 'bi-lock-fill',         text: 'End-to-End Encryption'      },
          { icon: 'bi-patch-check-fill',  text: 'ISO 27001 Compliant'        },
          { icon: 'bi-cloud-check-fill',  text: '99.9% Uptime SLA'           },
          { icon: 'bi-headset',           text: '24 / 7 Enterprise Support'  },
        ].map(({ icon, text }) => (
          <div key={text} className="trust-item">
            <i className={`bi ${icon} trust-icon`}></i>
            <span className="trust-text">{text}</span>
          </div>
        ))}
      </motion.div>

    </div>
  </section>
);

export default Statistics;
