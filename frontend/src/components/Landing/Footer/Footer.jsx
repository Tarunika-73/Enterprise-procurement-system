import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import './Footer.css';

const LINKS = {
  Platform: [
    { label: 'Features',          href: '#modules'    },
    { label: 'Workflow',          href: '#workflow'   },
    { label: 'Analytics',         href: '#analytics'  },
    { label: 'Dashboard Preview', href: '#dashboards' },
    { label: 'Pricing',           href: '#'           },
  ],
  Company: [
    { label: 'About Us',    href: '#about'   },
    { label: 'Careers',     href: '#'        },
    { label: 'Blog',        href: '#'        },
    { label: 'Press',       href: '#'        },
    { label: 'Contact',     href: '#contact' },
  ],
  Support: [
    { label: 'Documentation', href: '#' },
    { label: 'API Reference',  href: '#' },
    { label: 'Status Page',    href: '#' },
    { label: 'Help Centre',    href: '#' },
    { label: 'Community',      href: '#' },
  ],
  Legal: [
    { label: 'Privacy Policy',    href: '#' },
    { label: 'Terms of Service',  href: '#' },
    { label: 'Cookie Policy',     href: '#' },
    { label: 'Security',          href: '#' },
    { label: 'Compliance',        href: '#' },
  ],
};

const SOCIALS = [
  { icon: 'bi-linkedin',  href: '#', label: 'LinkedIn'  },
  { icon: 'bi-twitter-x', href: '#', label: 'Twitter'   },
  { icon: 'bi-github',    href: '#', label: 'GitHub'    },
  { icon: 'bi-youtube',   href: '#', label: 'YouTube'   },
];

const Footer = () => (
  <footer className="footer-section">
    <div className="footer-container">

      {/* ── Brand column ── */}
      <motion.div
        className="footer-brand"
        initial={{ opacity: 0, y: 24 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.4 }}
        transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
      >
        <a href="#home" className="footer-logo">
          <span className="footer-logo-icon">
            <i className="bi bi-box-seam-fill"></i>
          </span>
          <span className="footer-logo-text">
            Enterprise<span className="footer-logo-accent">Procure</span>
          </span>
        </a>

        <p className="footer-brand-desc">
          The intelligent procurement platform built for modern enterprises.
          From requisition to payment — fully automated, fully auditable.
        </p>

        {/* Social links */}
        <div className="footer-socials">
          {SOCIALS.map(({ icon, href, label }) => (
            <a key={label} href={href} className="footer-social" aria-label={label}>
              <i className={`bi ${icon}`}></i>
            </a>
          ))}
        </div>

        {/* Auth CTAs */}
        <div className="footer-auth">
          <Link to="/register" className="footer-btn-register">Get Started</Link>
          <Link to="/login"    className="footer-btn-login">Sign In</Link>
        </div>
      </motion.div>

      {/* ── Link columns ── */}
      <motion.div
        className="footer-links-grid"
        initial={{ opacity: 0, y: 24 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.3 }}
        transition={{ duration: 0.6, delay: 0.1, ease: [0.22, 1, 0.36, 1] }}
      >
        {Object.entries(LINKS).map(([group, items]) => (
          <div key={group} className="footer-link-col">
            <h4 className="footer-col-title">{group}</h4>
            <ul className="footer-col-list">
              {items.map(({ label, href }) => (
                <li key={label}>
                  <a href={href} className="footer-link">{label}</a>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </motion.div>

    </div>

    {/* ── Bottom bar ── */}
    <div className="footer-bottom">
      <div className="footer-bottom-inner">
        <span className="footer-copy">
          © {new Date().getFullYear()} EnterpriseProcure. All rights reserved.
        </span>
        <div className="footer-bottom-badges">
          <span className="footer-badge-pill">
            <i className="bi bi-shield-fill-check"></i> SOC 2
          </span>
          <span className="footer-badge-pill">
            <i className="bi bi-patch-check-fill"></i> ISO 27001
          </span>
          <span className="footer-badge-pill">
            <i className="bi bi-lock-fill"></i> GDPR Ready
          </span>
        </div>
      </div>
    </div>
  </footer>
);

export default Footer;
