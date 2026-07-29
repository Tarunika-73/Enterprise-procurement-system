import { useRef } from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import './Features.css';

const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.10, delayChildren: delay } },
});

const FEATURES = [
  { icon: 'bi-robot',              colorCls: 'fn-primary',   title: 'AI Procurement Assistant',   desc: 'Smart suggestions for vendor selection, budget optimisation, and anomaly detection powered by procurement data.' },
  { icon: 'bi-diagram-3-fill',     colorCls: 'fn-secondary', title: 'Automated Approval Routing',  desc: 'Rules-based routing sends requests to the right approver at the right level — zero manual intervention.' },
  { icon: 'bi-stars',              colorCls: 'fn-accent',    title: 'Supplier Intelligence',       desc: 'Aggregate vendor ratings, compliance scores, and delivery history into a single performance scorecard.' },
  { icon: 'bi-bell-fill',          colorCls: 'fn-green',     title: 'Real-Time Notifications',     desc: 'Instant alerts for approvals, PO status changes, invoice due dates, and compliance expirations.' },
  { icon: 'bi-graph-up-arrow',     colorCls: 'fn-amber',     title: 'Spend Analytics',             desc: 'Drill into spend by department, category, vendor, and time period with interactive dashboards.' },
  { icon: 'bi-piggy-bank-fill',    colorCls: 'fn-primary',   title: 'Budget Monitoring',           desc: 'Real-time budget utilisation tracking with threshold alerts before overspend occurs.' },
  { icon: 'bi-shield-exclamation', colorCls: 'fn-accent',    title: 'Vendor Risk Analysis',        desc: 'Identify high-risk suppliers through compliance expiry tracking, performance trends, and audit flags.' },
];

/* Triple the array so the seamless loop never shows a gap */
const CAROUSEL_ITEMS = [...FEATURES, ...FEATURES, ...FEATURES];

const ORBIT_RADIUS = 210;
const ANGLES = [0, 51, 103, 154, 206, 257, 309];

const Features = () => {
  const reduced  = useReducedMotion();
  const trackRef = useRef(null);

  return (
    <section className="features-section" id="features">
      <div className="feat-orb feat-orb-1" aria-hidden="true" />
      <div className="feat-orb feat-orb-2" aria-hidden="true" />

      <div className="features-container">

        {/* ── Header ── */}
        <motion.div
          className="features-header"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.5 }}
          variants={stagger(0)}
        >
          <motion.span className="section-badge" variants={fadeUp}>
            <i className="bi bi-cpu-fill"></i> Advanced Features
          </motion.span>

          <motion.h2 className="features-title" variants={fadeUp}>
            Intelligence Built<br />
            <span className="gradient-text">Into Every Step</span>
          </motion.h2>

          <motion.p className="features-desc" variants={fadeUp}>
            Beyond basic procurement — seven advanced capabilities that give
            your team a strategic edge through automation, analytics, and
            real-time intelligence.
          </motion.p>
        </motion.div>

        {/* ── Orbital illustration (centred) ── */}
        

        {/* ── Horizontal auto-scrolling carousel ── */}
        <motion.div
          className="carousel-section"
          initial={{ opacity: 0, y: 32 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.2 }}
          transition={{ duration: 0.7, ease: [0.22, 1, 0.36, 1] }}
        >
          <div className="carousel-fade carousel-fade-left"  aria-hidden="true" />
          <div className="carousel-fade carousel-fade-right" aria-hidden="true" />

          <div className="carousel-viewport">
            <div
              ref={trackRef}
              className={`carousel-track${reduced ? ' carousel-track--paused' : ''}`}
            >
              {CAROUSEL_ITEMS.map(({ icon, colorCls, title, desc }, i) => (
                <div
                  key={`${title}-${i}`}
                  className="carousel-card"
                  onMouseEnter={() => trackRef.current?.classList.add('carousel-track--paused')}
                  onMouseLeave={() => !reduced && trackRef.current?.classList.remove('carousel-track--paused')}
                >
                  <div className={`carousel-card-icon ${colorCls}`}>
                    <i className={`bi ${icon}`}></i>
                  </div>
                  <h4 className="carousel-card-title">{title}</h4>
                  <p className="carousel-card-desc">{desc}</p>
                </div>
              ))}
            </div>
          </div>
        </motion.div>

      </div>
    </section>
  );
};

export default Features;
