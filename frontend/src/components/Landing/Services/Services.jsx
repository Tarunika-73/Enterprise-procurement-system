import { useRef } from 'react';
import { motion, useScroll, useTransform, useReducedMotion } from 'framer-motion';
import './Services.css';

/* ── Animation helpers ── */
const fadeUp = {
  hidden:  { opacity: 0, y: 32 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.10, delayChildren: delay } },
});

/* ── Workflow steps ── */
const STEPS = [
  {
    icon: 'bi-person-fill',
    colorCls: 'step-primary',
    role: 'Employee',
    action: 'Raises Purchase Request',
    desc: 'Submits a structured PR with line items, quantities, and justification.',
  },
  {
    icon: 'bi-person-badge-fill',
    colorCls: 'step-secondary',
    role: 'Manager',
    action: 'Reviews & Approves',
    desc: 'Department manager reviews the request and approves or rejects with comments.',
  },
  {
    icon: 'bi-bank2',
    colorCls: 'step-accent',
    role: 'Finance',
    action: 'Budget Verification',
    desc: 'Finance team verifies budget availability and gives final approval.',
  },
  {
    icon: 'bi-bag-check-fill',
    colorCls: 'step-green',
    role: 'Procurement',
    action: 'Purchase Order Created',
    desc: 'A PO is auto-generated and sent to the selected vendor for acceptance.',
  },
  {
    icon: 'bi-shop',
    colorCls: 'step-amber',
    role: 'Vendor',
    action: 'Accepts & Fulfils',
    desc: 'Vendor accepts the PO, ships goods, and submits an invoice.',
  },
  {
    icon: 'bi-truck',
    colorCls: 'step-primary',
    role: 'Warehouse',
    action: 'Delivery & Receipt',
    desc: 'Goods are received, inspected, and a GRN is recorded in the system.',
  },
  {
    icon: 'bi-credit-card-fill',
    colorCls: 'step-green',
    role: 'Finance',
    action: 'Payment Processed',
    desc: 'Invoice is matched against PO and GRN; payment is released to the vendor.',
  },
];

const Workflow = () => {
  const sectionRef = useRef(null);
  const reduced = useReducedMotion();
  const { scrollYProgress } = useScroll({
    target: sectionRef,
    offset: ['start end', 'end start'],
  });

  /* progress line grows as user scrolls through the section */
  const lineWidth = useTransform(scrollYProgress, [0.1, 0.8], reduced ? ['100%', '100%'] : ['0%', '100%']);

  return (
    <section className="workflow-section" id="workflow" ref={sectionRef}>
      {/* background orbs */}
      <div className="wf-orb wf-orb-1" aria-hidden="true" />
      <div className="wf-orb wf-orb-2" aria-hidden="true" />

      <div className="workflow-container">

        {/* ── Header ── */}
        <motion.div
          className="workflow-header"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.5 }}
          variants={stagger(0)}
        >
          <motion.span className="section-badge" variants={fadeUp}>
            <i className="bi bi-diagram-3-fill"></i> Procurement Workflow
          </motion.span>

          <motion.h2 className="workflow-title" variants={fadeUp}>
            From Request to Payment,<br />
            <span className="gradient-text">Fully Automated</span>
          </motion.h2>

          <motion.p className="workflow-desc" variants={fadeUp}>
            Seven seamless steps connect every stakeholder — employee, manager,
            finance, vendor, and warehouse — in one unified, auditable flow.
          </motion.p>
        </motion.div>

        {/* ── Timeline track ── */}
        <div className="workflow-track-wrap">
          {/* animated progress line */}
          <div className="wf-track-bg" aria-hidden="true">
            <motion.div className="wf-track-fill" style={{ width: lineWidth }} />
          </div>

          {/* Steps */}
          <motion.div
            className="workflow-steps"
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, amount: 0.15 }}
            variants={stagger(0.06)}
          >
            {STEPS.map(({ icon, colorCls, role, action, desc }, i) => (
              <motion.div
                key={role + i}
                className="wf-step"
                variants={fadeUp}
              >
                {/* connector dot on the track */}
                <motion.div
                  className={`wf-dot ${colorCls}`}
                  initial={{ scale: 0 }}
                  whileInView={{ scale: 1 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.4, delay: 0.1 + i * 0.10, ease: [0.22, 1, 0.36, 1] }}
                  whileHover={reduced ? {} : { scale: 1.25 }}
                >
                  <i className={`bi ${icon}`}></i>
                </motion.div>

                {/* card — alternates above/below the track */}
                <motion.div
                  className={`wf-card ${i % 2 === 0 ? 'wf-card-top' : 'wf-card-bottom'}`}
                  whileHover={reduced ? {} : { y: i % 2 === 0 ? -6 : 6, transition: { duration: 0.25, ease: 'easeOut' } }}
                >
                  <span className={`wf-step-num ${colorCls}`}>{String(i + 1).padStart(2, '0')}</span>
                  <span className="wf-role">{role}</span>
                  <h4 className="wf-action">{action}</h4>
                  <p className="wf-desc">{desc}</p>
                </motion.div>

                {/* arrow connector between steps */}
                {i < STEPS.length - 1 && (
                  <div className="wf-arrow" aria-hidden="true">
                    <i className="bi bi-chevron-right"></i>
                  </div>
                )}
              </motion.div>
            ))}
          </motion.div>
        </div>

        {/* ── Bottom stat strip ── */}
        <motion.div
          className="wf-stats-strip"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.5 }}
          variants={stagger(0.08)}
        >
          {[
            { icon: 'bi-clock-fill',        value: '< 24h',  label: 'Average Approval Time'  },
            { icon: 'bi-arrow-repeat',       value: '100%',   label: 'End-to-End Automation'  },
            { icon: 'bi-shield-check',       value: 'Zero',   label: 'Compliance Gaps'        },
            { icon: 'bi-graph-down-arrow',   value: '40%',    label: 'Reduction in Cycle Time' },
          ].map(({ icon, value, label }) => (
            <motion.div key={label} className="wf-stat" variants={fadeUp}>
              <i className={`bi ${icon} wf-stat-icon`}></i>
              <span className="wf-stat-value">{value}</span>
              <span className="wf-stat-label">{label}</span>
            </motion.div>
          ))}
        </motion.div>

      </div>
    </section>
  );
};

export default Workflow;
