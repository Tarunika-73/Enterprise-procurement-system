import { useState } from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import './Modules.css';

/* ── Animation helpers ── */
const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.08, delayChildren: delay } },
});

/* ── Module data ── */
const MODULES = [
  {
    icon: 'bi-people-fill',
    colorCls: 'mod-primary',
    title: 'Vendor Management',
    desc: 'Onboard, evaluate, and manage your entire supplier network with compliance tracking and performance scoring.',
    tags: ['Onboarding', 'Compliance', 'Ratings'],
  },
  {
    icon: 'bi-file-earmark-text-fill',
    colorCls: 'mod-accent',
    title: 'Purchase Requisition',
    desc: 'Employees raise structured purchase requests with line items, justifications, and estimated costs in seconds.',
    tags: ['Line Items', 'Justification', 'Draft & Submit'],
  },
  {
    icon: 'bi-check2-circle',
    colorCls: 'mod-secondary',
    title: 'Approval Workflow',
    desc: 'Multi-level approval routing with escalation rules, delegation, and full audit trail for every decision.',
    tags: ['Multi-level', 'Escalation', 'Audit Trail'],
  },
  {
    icon: 'bi-bag-check-fill',
    colorCls: 'mod-green',
    title: 'Purchase Orders',
    desc: 'Auto-generate POs from approved requests, send to vendors, and track acceptance and fulfilment status.',
    tags: ['Auto-generate', 'Vendor Send', 'Tracking'],
  },
  {
    icon: 'bi-receipt-cutoff',
    colorCls: 'mod-amber',
    title: 'Invoices & Receipts',
    desc: 'Match invoices against POs and delivery receipts with three-way matching to prevent overpayment.',
    tags: ['3-Way Match', 'GRN', 'Discrepancy Alerts'],
  },
  {
    icon: 'bi-credit-card-fill',
    colorCls: 'mod-primary',
    title: 'Payments',
    desc: 'Process vendor payments with full traceability, payment method tracking, and status management.',
    tags: ['Traceability', 'Methods', 'Status'],
  },
  {
    icon: 'bi-truck',
    colorCls: 'mod-secondary',
    title: 'Delivery Tracking',
    desc: 'Monitor shipment status, carrier details, and delivery confirmation with real-time updates.',
    tags: ['Carrier', 'Tracking No.', 'Confirmation'],
  },
  {
    icon: 'bi-bar-chart-line-fill',
    colorCls: 'mod-accent',
    title: 'Supplier Performance',
    desc: 'Rate vendors on quality, delivery, and pricing after every order to build a data-driven supplier scorecard.',
    tags: ['Quality', 'Delivery', 'Pricing Score'],
  },
  {
    icon: 'bi-graph-up-arrow',
    colorCls: 'mod-green',
    title: 'Analytics & Reports',
    desc: 'Spend analytics, budget utilisation, vendor benchmarking, and procurement KPIs in real-time dashboards.',
    tags: ['Spend', 'KPIs', 'Dashboards'],
  },
  {
    icon: 'bi-bell-fill',
    colorCls: 'mod-amber',
    title: 'Notifications',
    desc: 'Automated alerts for approvals, PO status changes, invoice due dates, and compliance expirations.',
    tags: ['Email', 'In-App', 'Alerts'],
  },
];

const Modules = () => {
  const [hovered, setHovered] = useState(null);
  const reduced = useReducedMotion();

  return (
    <section className="modules-section" id="modules">
      {/* background orbs */}
      <div className="modules-orb modules-orb-1" aria-hidden="true" />
      <div className="modules-orb modules-orb-2" aria-hidden="true" />

      <div className="modules-container">

        {/* ── Header ── */}
        <motion.div
          className="modules-header"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.5 }}
          variants={stagger(0)}
        >
          <motion.span className="section-badge" variants={fadeUp}>
            <i className="bi bi-grid-3x3-gap-fill"></i> Core Modules
          </motion.span>

          <motion.h2 className="modules-title" variants={fadeUp}>
            Everything You Need,<br />
            <span className="gradient-text">In One Platform</span>
          </motion.h2>

          <motion.p className="modules-desc" variants={fadeUp}>
            Ten purpose-built modules covering the complete procurement
            lifecycle — from the first purchase request to the final
            vendor payment.
          </motion.p>
        </motion.div>

        {/* ── Module cards grid ── */}
        <motion.div
          className="modules-grid"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.1 }}
          variants={stagger(0.04)}
        >
          {MODULES.map(({ icon, colorCls, title, desc, tags }, i) => (
            <motion.div
              key={title}
              className={`module-card${hovered === i ? ' is-hovered' : ''}`}
              variants={fadeUp}
              onHoverStart={() => setHovered(i)}
              onHoverEnd={() => setHovered(null)}
              whileHover={reduced ? {} : { y: -8, scale: 1.02, transition: { duration: 0.28, ease: 'easeOut' } }}
            >
              {/* icon */}
              <div className={`module-icon ${colorCls}`}>
                <i className={`bi ${icon}`}></i>
              </div>

              {/* text */}
              <div className="module-body">
                <h3 className="module-title">{title}</h3>
                <p className="module-desc">{desc}</p>
              </div>

              {/* tags */}
              <div className="module-tags">
                {tags.map(tag => (
                  <span key={tag} className="module-tag">{tag}</span>
                ))}
              </div>

              {/* hover glow */}
              <div className="module-glow" aria-hidden="true" />

              {/* gradient border reveal */}
              <div className="module-border-gradient" aria-hidden="true" />
            </motion.div>
          ))}
        </motion.div>

        {/* ── Bottom CTA strip ── */}
        <motion.div
          className="modules-cta"
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.6 }}
          transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
        >
          <span className="modules-cta-text">
            All modules work together seamlessly — no integrations required.
          </span>
          <a href="#workflow" className="modules-cta-link">
            See how it flows <i className="bi bi-arrow-right"></i>
          </a>
        </motion.div>

      </div>
    </section>
  );
};

export default Modules;
