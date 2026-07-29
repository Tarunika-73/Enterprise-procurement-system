import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import './Navbar.css';

const NAV_LINKS = [
  { label: "Home", href: "#home" },
  { label: "About", href: "#about" },
  { label: "Analytics", href: "#analytics" },
  { label: "Features", href: "#features" },
  { label: "Contact", href: "#contact" },
];

const Navbar = () => {
  const [scrolled,  setScrolled]  = useState(false);
  const [menuOpen,  setMenuOpen]  = useState(false);
  const [activeLink, setActiveLink] = useState('');

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 30);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleNavClick = (href) => {
    setActiveLink(href);
    setMenuOpen(false);
  };

  return (
    <div className="navbar-wrapper">
    <motion.nav
      className={`landing-navbar${scrolled ? ' scrolled' : ''}`}
      initial={{ y: -80, opacity: 0 }}
      animate={{ y: 0,   opacity: 1 }}
      transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="navbar-container">

        {/* ── Logo ── */}
        <a href="#home" className="navbar-logo" onClick={() => handleNavClick('#home')}>
          <span className="logo-icon">
            <i className="bi bi-box-seam-fill"></i>
          </span>
          <span className="logo-text">
            Enterprise<span className="logo-accent">Procure</span>
          </span>
        </a>

        {/* ── Desktop nav links ── */}
        <ul className="navbar-links">
          {NAV_LINKS.map(({ label, href }) => (
            <li key={label}>
              <a
                href={href}
                className={`nav-link${activeLink === href ? ' active' : ''}`}
                onClick={() => handleNavClick(href)}
              >
                {label}
                <span className="nav-link-dot" />
              </a>
            </li>
          ))}
        </ul>

        {/* ── Desktop auth buttons ── */}
        <div className="navbar-auth">
          <Link to="/login" className="btn-nav-login">Login</Link>
          <Link to="/register" className="btn-nav-register">Register</Link>
        </div>

        {/* ── Hamburger ── */}
        <button
          className={`hamburger${menuOpen ? ' active' : ''}`}
          onClick={() => setMenuOpen(prev => !prev)}
          aria-label="Toggle menu"
        >
          <span /><span /><span />
        </button>
      </div>

      {/* ── Mobile drawer ── */}
      <AnimatePresence>
        {menuOpen && (
          <motion.div
            className="mobile-menu"
            initial={{ opacity: 0, y: -12, scale: 0.97 }}
            animate={{ opacity: 1, y: 0,   scale: 1    }}
            exit={{    opacity: 0, y: -12, scale: 0.97 }}
            transition={{ duration: 0.25, ease: 'easeOut' }}
          >
            <ul className="mobile-links">
              {NAV_LINKS.map(({ label, href }, i) => (
                <motion.li
                  key={label}
                  initial={{ opacity: 0, x: -16 }}
                  animate={{ opacity: 1, x: 0   }}
                  transition={{ delay: i * 0.05, duration: 0.25, ease: 'easeOut' }}
                >
                  <a
                    href={href}
                    className="mobile-nav-link"
                    onClick={() => handleNavClick(href)}
                  >
                    {label}
                  </a>
                </motion.li>
              ))}
            </ul>
            <div className="mobile-auth">
              <Link to="/login"    className="btn-nav-login  w-100" onClick={() => setMenuOpen(false)}>Login</Link>
              <Link to="/register" className="btn-nav-register w-100" onClick={() => setMenuOpen(false)}>Register</Link>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.nav>
    </div>
  );
};

export default Navbar;
