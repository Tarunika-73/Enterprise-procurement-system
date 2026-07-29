import { useRef } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import procurementIllustration from "../../../assets/hero.png";
import "./Hero.css";

const fadeUp = (delay = 0) => ({
  hidden: { opacity: 0, y: 40 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.7,
      delay,
      ease: [0.22, 1, 0.36, 1],
    },
  },
});

const Hero = () => {
  const sectionRef = useRef(null);

  return (
    <section className="hero-section" id="home" ref={sectionRef}>
      {/* Background Effects */}
      <div className="hero-blob hero-blob-1"></div>
      <div className="hero-blob hero-blob-2"></div>
      <div className="hero-blob hero-blob-3"></div>

      {/* Floating particles */}
      <div className="hero-particles">
        {Array.from({ length: 18 }).map((_, i) => (
          <span key={i} className={`particle particle-${i + 1}`} />
        ))}
      </div>

      <div className="hero-container">

        {/* LEFT CONTENT */}
        <motion.div
          className="hero-content"
          initial="hidden"
          animate="visible"
          variants={{
            visible: {
              transition: {
                staggerChildren: 0.12,
              },
            },
          }}
        >
          <motion.span className="hero-badge" variants={fadeUp(0)}>
            <i className="bi bi-stars"></i>
            Enterprise-Grade Procurement
          </motion.span>

          <motion.h1 className="hero-title" variants={fadeUp(0.05)}>
            Smart Enterprise
            <br />
            <span className="hero-title-accent">Procurement</span> Platform
          </motion.h1>

          <motion.p className="hero-subtitle" variants={fadeUp(0.1)}>
            Streamline enterprise purchasing with intelligent procurement
            workflows, automated approvals, supplier collaboration,
            purchase order management, financial integration and
            real-time analytics.
          </motion.p>

          <motion.div className="hero-actions" variants={fadeUp(0.15)}>
            <Link to="/register" className="hero-btn-primary">
              Get Started
              <i className="bi bi-arrow-right"></i>
            </Link>

            <a href="#modules" className="hero-btn-secondary">
              Explore Features
              <i className="bi bi-compass"></i>
            </a>
          </motion.div>

          <motion.div className="hero-stats" variants={fadeUp(0.2)}>
            {[
              {
                value: "500+",
                label: "Enterprises",
              },
              {
                value: "98%",
                label: "Efficiency Gain",
              },
              {
                value: "40%",
                label: "Cost Reduction",
              },
            ].map(({ value, label }, i) => (
              <div key={label} className="hero-stat-group">
                {i > 0 && <div className="hero-stat-divider"></div>}

                <div className="hero-stat">
                  <span className="hero-stat-value">{value}</span>
                  <span className="hero-stat-label">{label}</span>
                </div>
              </div>
            ))}
          </motion.div>
        </motion.div>

        {/* RIGHT IMAGE */}
        <motion.div
          className="hero-illustration"
          initial={{
            opacity: 0,
            x: 60,
          }}
          animate={{
            opacity: 1,
            x: 0,
          }}
          transition={{
            duration: 0.9,
            delay: 0.3,
          }}
        >
          <img
            src={procurementIllustration}
            alt="Enterprise Procurement"
            className="hero-procurement-img"
          />
        </motion.div>

      </div>

      {/* Scroll Indicator */}
      <motion.div
        className="hero-scroll-cue"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{
          delay: 1.4,
        }}
      >
        <motion.div
          className="scroll-dot"
          animate={{
            y: [0, 8, 0],
          }}
          transition={{
            duration: 1.4,
            repeat: Infinity,
          }}
        />
      </motion.div>
    </section>
  );
};

export default Hero;