import { motion, useReducedMotion } from 'framer-motion';
import { Link } from 'react-router-dom';
import './Contact.css';

const fadeUp = {
  hidden:  { opacity: 0, y: 36 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = (delay = 0) => ({
  hidden:  {},
  visible: { transition: { staggerChildren: 0.11, delayChildren: delay } },
});

const CONTACT_ITEMS = [
  { icon: 'bi-envelope-fill',    label: 'Email Us',    value: 'support@enterpriseprocure.com', href: 'mailto:support@enterpriseprocure.com' },
  { icon: 'bi-telephone-fill',   label: 'Call Us',     value: '+91 98765 43210',               href: 'tel:+919876543210'                   },
  { icon: 'bi-geo-alt-fill',     label: 'Visit Us',    value: 'Bengaluru, Karnataka, India',   href: '#'                                   },
  { icon: 'bi-headset',          label: 'Support',     value: '24 / 7 Enterprise Support',     href: '#'                                   },
];

const Contact = () => {
  const reduced = useReducedMotion();
  return (
  <section className="contact-section" id="contact">
    {/* gradient bg */}
    <div className="contact-bg" aria-hidden="true" />
    <div className="contact-orb contact-orb-1" aria-hidden="true" />
    <div className="contact-orb contact-orb-2" aria-hidden="true" />

    <div className="contact-container">

      {/* ══════════════════════════════
          Big closing CTA
          ══════════════════════════════ */}
      <motion.div
        className="contact-cta-block"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.4 }}
        variants={stagger(0)}
      >
        <motion.span className="contact-badge" variants={fadeUp}>
          <i className="bi bi-rocket-takeoff-fill"></i> Get Started Today
        </motion.span>

        <motion.h2 className="contact-cta-title" variants={fadeUp}>
          Ready to Transform<br />
          <span className="contact-gradient-text">Your Procurement?</span>
        </motion.h2>

        <motion.p className="contact-cta-desc" variants={fadeUp}>
          Join hundreds of enterprises already running smarter, faster, and
          more compliant procurement operations on our platform.
        </motion.p>

        <motion.div className="contact-cta-actions" variants={fadeUp}>
          <Link to="/register" className="cta-btn-primary">
            Start Free Trial
            <i className="bi bi-arrow-right"></i>
          </Link>
          <Link to="/login" className="cta-btn-secondary">
            Sign In
          </Link>
        </motion.div>

        <motion.div className="contact-cta-trust" variants={fadeUp}>
          {['No credit card required', 'Setup in under 5 minutes', 'Cancel anytime'].map(t => (
            <span key={t} className="cta-trust-item">
              <i className="bi bi-check-circle-fill"></i> {t}
            </span>
          ))}
        </motion.div>
      </motion.div>

      {/* ══════════════════════════════
          Contact info cards
          ══════════════════════════════ */}
      <motion.div
        className="contact-info-grid"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.2 }}
        variants={stagger(0.07)}
      >
        {CONTACT_ITEMS.map(({ icon, label, value, href }) => (
          <motion.a
            key={label}
            href={href}
            className="contact-info-card"
            variants={fadeUp}
            whileHover={reduced ? {} : { y: -6, scale: 1.02, transition: { duration: 0.25, ease: 'easeOut' } }}
          >
            <div className="contact-info-icon">
              <i className={`bi ${icon}`}></i>
            </div>
            <div className="contact-info-text">
              <span className="contact-info-label">{label}</span>
              <span className="contact-info-value">{value}</span>
            </div>
            <i className="bi bi-arrow-up-right contact-info-arrow"></i>
          </motion.a>
        ))}
      </motion.div>

    </div>
  </section>
  );
};

export default Contact;
