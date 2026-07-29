import { motion, useReducedMotion } from 'framer-motion';
import './About.css';

/* ── Animation helpers ── */
const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.7, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.13, delayChildren: delay } },
});

/* ── Data ── */
const MV_CARDS = [
  {
    icon: 'bi-bullseye',
    colorCls: 'icon-primary',
    label: 'Our Mission',
    body: 'To simplify enterprise procurement by delivering an intelligent, integrated platform that eliminates manual bottlenecks, reduces costs, and gives procurement teams the tools they need to make smarter decisions faster.',
  },
  {
    icon: 'bi-binoculars-fill',
    colorCls: 'icon-accent',
    label: 'Our Vision',
    body: 'To become the most trusted procurement platform for enterprises worldwide — where every purchase is transparent, every vendor relationship is optimised, and every rupee spent delivers measurable value.',
  },
  {
    icon: 'bi-people-fill',
    colorCls: 'icon-secondary',
    label: 'Our Values',
    body: 'Transparency, accountability, and continuous improvement drive everything we build. We believe procurement should be a strategic advantage — not an administrative burden — for every organisation.',
  },
];

const BENEFITS = [
  
];

const About = () => {
  const reduced = useReducedMotion();
  return (
  <section className="about-section" id="about">
    {/* background orbs */}
    <div className="about-orb about-orb-1" aria-hidden="true" />
    <div className="about-orb about-orb-2" aria-hidden="true" />

    <div className="about-container">

      {/* ══════════════════════════════
          Section header
          ══════════════════════════════ */}
      <motion.div
        className="about-header"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.4 }}
        variants={stagger(0)}
      >
        <motion.span className="section-badge" variants={fadeUp}>
          <i className="bi bi-info-circle-fill"></i> About Us
        </motion.span>

        <motion.h2 className="about-title" variants={fadeUp}>
          Built for{' '}
          <span className="gradient-text">Enterprise</span>{' '}
          Procurement
        </motion.h2>

        <motion.p className="about-desc" variants={fadeUp}>
          The Enterprise Procurement System is a unified platform designed to
          digitise and automate the end-to-end procurement process — from
          requisition to payment — empowering organisations to operate with
          greater speed, control, and transparency.
        </motion.p>
      </motion.div>

      {/* ══════════════════════════════
          Mission / Vision / Values — 3 glass cards
          ══════════════════════════════ */}
      <motion.div
        className="about-mv-grid"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.2 }}
        variants={stagger(0.05)}
      >
        {MV_CARDS.map(({ icon, colorCls, label, body }) => (
          <motion.div
            key={label}
            className="about-mv-card"
            variants={fadeUp}
            whileHover={reduced ? {} : { y: -6, transition: { duration: 0.25, ease: 'easeOut' } }}
          >
            <div className={`about-mv-icon ${colorCls}`}>
              <i className={`bi ${icon}`}></i>
            </div>
            <h3 className="about-mv-label">{label}</h3>
            <p className="about-mv-body">{body}</p>

            {/* gradient border glow on hover via pseudo — handled in CSS */}
            <div className="card-glow" aria-hidden="true" />
          </motion.div>
        ))}
      </motion.div>

      {/* ══════════════════════════════
          Benefits — asymmetric split
          ══════════════════════════════ */}
      <div className="about-benefits-wrap">

        {/* Left: large text block */}
        <motion.div
          className="about-benefits-text"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.3 }}
          variants={stagger(0)}
        >
          {/* <motion.span className="section-badge" variants={fadeUp}>
            <i className="bi bi-stars"></i> Why Choose Us
          </motion.span>
          <motion.h2 className="about-benefits-title" variants={fadeUp}>
            Why Teams<br />
            <span className="gradient-text">Choose Us</span>
          </motion.h2>
          <motion.p className="about-benefits-sub" variants={fadeUp}>
            Thousands of procurement professionals rely on our platform every
            day to manage spend, collaborate with vendors, and keep their
            organisations audit-ready.
          </motion.p> */}

          {/* <motion.div className="about-trust-row" variants={fadeUp}>
            <div className="trust-pill">
              <i className="bi bi-patch-check-fill"></i> ISO 27001 Compliant
            </div>
            <div className="trust-pill">
              <i className="bi bi-lock-fill"></i> SOC 2 Type II
            </div>
          </motion.div> */}
        </motion.div>

        {/* Right: 2×2 benefit cards */}
        <motion.div
          className="about-benefits-grid"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.2 }}
          variants={stagger(0.08)}
        >
          {BENEFITS.map(({ icon, colorCls, title, desc }) => (
            <motion.div
              key={title}
              className="about-benefit-card"
              variants={fadeUp}
              whileHover={reduced ? {} : { y: -5, transition: { duration: 0.22, ease: 'easeOut' } }}
            >
              <div className={`benefit-icon-wrap ${colorCls}`}>
                <i className={`bi ${icon}`}></i>
              </div>
              <h4 className="benefit-title">{title}</h4>
              <p className="benefit-desc">{desc}</p>
            </motion.div>
          ))}
        </motion.div>
      </div>

    </div>
  </section>
  );
};

export default About;
