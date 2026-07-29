import { motion } from 'framer-motion';
import './WhyChooseUs.css';

/* ── Brand tiles — enterprise-style company names ── */
const BRANDS = [
  { icon: 'bi-building',          name: 'Infosys'       },
  { icon: 'bi-globe2',            name: 'TCS'           },
  { icon: 'bi-cpu-fill',          name: 'Wipro'         },
  { icon: 'bi-diagram-3-fill',    name: 'HCL Tech'      },
  { icon: 'bi-bar-chart-fill',    name: 'Tech Mahindra' },
  { icon: 'bi-shield-fill-check', name: 'Cognizant'     },
  { icon: 'bi-layers-fill',       name: 'Accenture'     },
  { icon: 'bi-briefcase-fill',    name: 'Deloitte'      },
  { icon: 'bi-bank2',             name: 'KPMG'          },
  { icon: 'bi-award-fill',        name: 'PwC'           },
];

/* Duplicate for seamless loop */
const TRACK = [...BRANDS, ...BRANDS];

const TrustedBy = () => (
  <section className="trustedby-section" aria-label="Trusted by enterprises">

    {/* ── Header ── */}
    <motion.div
      className="trustedby-header"
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.6 }}
      transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
    >
      <span className="trustedby-label">
        Trusted by leading enterprises worldwide
      </span>
    </motion.div>

    {/* ── Marquee track ── */}
    <div className="marquee-outer" aria-hidden="true">
      {/* left fade */}
      <div className="marquee-fade marquee-fade-left" />

      <div className="marquee-viewport">
        <motion.div
          className="marquee-track"
          animate={{ x: ['0%', '-50%'] }}
          transition={{ duration: 28, repeat: Infinity, ease: 'linear' }}
        >
          {TRACK.map(({ icon, name }, i) => (
            <div key={`${name}-${i}`} className="marquee-tile">
              <i className={`bi ${icon} marquee-tile-icon`}></i>
              <span className="marquee-tile-name">{name}</span>
            </div>
          ))}
        </motion.div>
      </div>

      {/* right fade */}
      <div className="marquee-fade marquee-fade-right" />
    </div>

  </section>
);

export default TrustedBy;
