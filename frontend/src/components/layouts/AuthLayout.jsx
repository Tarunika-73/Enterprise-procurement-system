import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Logo from '../Authentication/Logo/Logo';
import { APP_VERSION, COPYRIGHT } from '../../utils/constants';

const FEATURES = [
  { icon: 'bi-file-earmark-plus-fill', label: 'Purchase Requests',  desc: 'Create and track purchase requests effortlessly.'       },
  { icon: 'bi-bag-check-fill',         label: 'Purchase Orders',    desc: 'Manage and approve purchase orders in real time.'        },
  { icon: 'bi-truck',                  label: 'Vendor Delivery',    desc: 'Track deliveries and manage vendor performance.'         },
  { icon: 'bi-currency-dollar',        label: 'Finance Approval',   desc: 'Seamless finance approval and payment processing.'       },
];

const panelV = {
  hidden:  { opacity: 0, x: -28 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};
const formV = {
  hidden:  { opacity: 0, x: 28 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.65, ease: [0.22, 1, 0.36, 1] } },
};
const stagger = {
  hidden:  {},
  visible: { transition: { staggerChildren: 0.07, delayChildren: 0.18 } },
};
const fadeUp = {
  hidden:  { opacity: 0, y: 16 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.48, ease: [0.22, 1, 0.36, 1] } },
};

const AuthLayout = ({ children, variant = 'internal', template = 'default', copyVariant = 'login' }) => {
  const isVendor = variant === 'vendor';
  const isReferenceTemplate = template === 'reference';
  const isRegistration = copyVariant === 'register';

  return (
    <div className={`auth-shell${isReferenceTemplate ? ' auth-shell-reference' : ''}${isReferenceTemplate && isVendor ? ' auth-shell-reference-vendor' : ''}${isReferenceTemplate && isVendor && isRegistration ? ' auth-shell-reference-vendor-register' : ''}`}>

      {/* ── LEFT PANEL 55% ── */}
      <motion.div className="auth-left-panel" initial="hidden" animate="visible" variants={panelV}>
        <div className="auth-blob auth-blob-1" aria-hidden="true" />
        <div className="auth-blob auth-blob-2" aria-hidden="true" />
        <div className="auth-blob auth-blob-3" aria-hidden="true" />
        <div className="auth-dot-grid"         aria-hidden="true" />

        <motion.div className="auth-left-content" initial="hidden" animate="visible" variants={stagger}>

          {/* Logo */}
          <motion.div variants={fadeUp}>
            <Logo size="md" />
          </motion.div>

          {/* Heading */}
          <motion.div variants={fadeUp}>
            <h2 className="auth-left-title">
              {isReferenceTemplate ? (
                isVendor
                  ? isRegistration
                    ? <>Grow your supplier<br /><span className="auth-left-accent">partnership.</span></>
                    : <>Vendor<br /><span className="auth-left-accent">Portal.</span></>
                  : isRegistration
                  ? <>Build a better<br /><span className="auth-left-accent">procurement workflow.</span></>
                  : <>Manage procurement<br /><span className="auth-left-accent">the smarter way.</span></>
              ) : (
                <>Enterprise Procurement<br /><span className="auth-left-accent">System</span></>
              )}
            </h2>
            <p className="auth-left-desc">
              {isReferenceTemplate
                ? isVendor
                  ? isRegistration
                    ? 'Create a secure vendor account and collaborate with enterprise procurement teams.'
                    : 'Manage purchase orders, deliveries and procurement updates from one secure platform.'
                  : isRegistration
                  ? 'Connect teams, vendors and approvals in one streamlined enterprise platform.'
                  : 'Manage purchase requests, approvals, vendors and purchase orders from one secure platform.'
                : isVendor
                ? 'Secure vendor portal for purchase orders, invoicing, and supply chain collaboration.'
                : 'Streamline procurement workflows—from purchase requests to finance approval—in one unified platform.'}
            </p>
          </motion.div>

          {isReferenceTemplate && isVendor && (
            isRegistration ? (
              <motion.div className="auth-vendor-registration-visual" variants={fadeUp} aria-hidden="true">
                <svg viewBox="0 0 260 164" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="24" y="26" width="150" height="112" rx="20" fill="rgba(255,255,255,.55)" />
                  <rect x="24" y="26" width="150" height="112" rx="20" stroke="rgba(91,70,151,.18)" />
                  <rect x="45" y="46" width="64" height="74" rx="11" fill="#fff" />
                  <rect x="58" y="59" width="38" height="7" rx="3.5" fill="#9B87D8" />
                  <rect x="58" y="73" width="28" height="5" rx="2.5" fill="#D4C5F2" />
                  <rect x="58" y="84" width="32" height="5" rx="2.5" fill="#D4C5F2" />
                  <circle cx="67" cy="103" r="8" fill="#8067D8" opacity=".9" />
                  <path d="m63.5 103 2.5 2.5 4.5-5" stroke="white" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                  <rect x="121" y="61" width="34" height="34" rx="11" fill="#8067D8" />
                  <path d="M130 72h16l-2 10h-12l-2-14h-3" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  <circle cx="134" cy="87" r="2" fill="white" />
                  <circle cx="142" cy="87" r="2" fill="white" />
                  <g transform="translate(163 94)">
                    <rect width="68" height="39" rx="11" fill="#fff" />
                    <path d="M13 12h26v17H13zM39 18h9l7 7v4H39z" fill="#A18EE3" />
                    <circle cx="22" cy="31" r="4" fill="#5D4DA1" />
                    <circle cx="47" cy="31" r="4" fill="#5D4DA1" />
                  </g>
                  <rect x="187" y="40" width="42" height="42" rx="13" fill="#F7F3FF" stroke="rgba(91,70,151,.14)" />
                  <path d="M198 56h20M198 63h14" stroke="#8067D8" strokeWidth="2.4" strokeLinecap="round" />
                  <circle cx="214" cy="70" r="5" fill="#A18EE3" />
                </svg>
              </motion.div>
            ) : (
              <motion.div className="auth-vendor-portal-icon" variants={fadeUp} aria-hidden="true">
                <span className="auth-vendor-icon-main"><i className="bi bi-box-seam-fill" /></span>
                <span className="auth-vendor-icon-chip"><i className="bi bi-truck" /></span>
              </motion.div>
            )
          )}

          {/* Illustration */}
          <motion.div className="auth-illus-wrap" variants={fadeUp} aria-hidden="true">
            <svg viewBox="0 0 560 360" fill="none" xmlns="http://www.w3.org/2000/svg" className="auth-illus-svg">

              {/* ── outer card ── */}
              <rect x="16" y="16" width="528" height="328" rx="22" fill="#F5F3FF" />
              <rect x="16" y="16" width="528" height="328" rx="22" stroke="#E7E7F4" strokeWidth="1.5" />

              {/* ── top bar ── */}
              <rect x="16" y="16" width="528" height="54" rx="22" fill="#EDE9FE" />
              <rect x="16" y="48" width="528" height="22" fill="#EDE9FE" />
              <circle cx="50"  cy="43" r="9" fill="#6D5DF6" opacity="0.65" />
              <circle cx="76"  cy="43" r="9" fill="#A855F7" opacity="0.45" />
              <circle cx="102" cy="43" r="9" fill="#C4B5FD" opacity="0.45" />
              <rect x="130" y="35" width="110" height="16" rx="8" fill="#DDD6FE" />
              {/* search bar */}
              <rect x="360" y="31" width="160" height="24" rx="12" fill="white" opacity="0.7" />
              <circle cx="376" cy="43" r="5" stroke="#A78BFA" strokeWidth="1.5" fill="none" />
              <line x1="380" y1="47" x2="383" y2="50" stroke="#A78BFA" strokeWidth="1.5" strokeLinecap="round" />

              {/* ── sidebar ── */}
              <rect x="16" y="70" width="108" height="274" fill="#F0EEFF" />
              {/* active item */}
              <rect x="24" y="88"  width="92" height="30" rx="9" fill="#6D5DF6" opacity="0.13" />
              <rect x="24" y="88"  width="4"  height="30" rx="2" fill="#6D5DF6" />
              <rect x="36" y="99"  width="52" height="8"  rx="4" fill="#6D5DF6" opacity="0.55" />
              {/* other items */}
              {[128, 166, 204, 242, 280, 318].map((y, i) => (
                <g key={i}>
                  <rect x="24" y={y} width="92" height="28" rx="8" fill="#6D5DF6" opacity="0.04" />
                  <rect x="36" y={y + 10} width={28 + (i % 3) * 10} height="7" rx="3.5" fill="#C4B5FD" opacity="0.45" />
                </g>
              ))}

              {/* ── KPI cards row ── */}
              {/* Card 1 */}
              <rect x="136" y="82"  width="118" height="72" rx="14" fill="white" />
              <rect x="136" y="82"  width="118" height="72" rx="14" stroke="#E7E7F4" strokeWidth="1" />
              <rect x="150" y="95"  width="32"  height="32" rx="9"  fill="#EDE9FE" />
              <path d="M158 119 L162 112 L166 115 L172 107" stroke="#6D5DF6" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <rect x="150" y="133" width="60"  height="9"  rx="4.5" fill="#1E1E2F" opacity="0.65" />
              <rect x="150" y="146" width="40"  height="6"  rx="3"   fill="#C4B5FD" opacity="0.55" />

              {/* Card 2 */}
              <rect x="266" y="82"  width="118" height="72" rx="14" fill="white" />
              <rect x="266" y="82"  width="118" height="72" rx="14" stroke="#E7E7F4" strokeWidth="1" />
              <rect x="280" y="95"  width="32"  height="32" rx="9"  fill="#F3E8FF" />
              <path d="M286 119 L290 109 L294 113 L298 105 L302 109 L306 103" stroke="#A855F7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <rect x="280" y="133" width="60"  height="9"  rx="4.5" fill="#1E1E2F" opacity="0.65" />
              <rect x="280" y="146" width="44"  height="6"  rx="3"   fill="#DDD6FE" opacity="0.55" />

              {/* Card 3 */}
              <rect x="396" y="82"  width="132" height="72" rx="14" fill="white" />
              <rect x="396" y="82"  width="132" height="72" rx="14" stroke="#E7E7F4" strokeWidth="1" />
              <rect x="410" y="95"  width="32"  height="32" rx="9"  fill="#ECFDF5" />
              <path d="M416 119 L420 113 L424 116 L428 109 L432 111 L436 105" stroke="#10B981" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <rect x="410" y="133" width="68"  height="9"  rx="4.5" fill="#1E1E2F" opacity="0.65" />
              <rect x="410" y="146" width="48"  height="6"  rx="3"   fill="#A7F3D0" opacity="0.55" />

              {/* ── Bar chart card ── */}
              <rect x="136" y="166" width="220" height="148" rx="14" fill="white" />
              <rect x="136" y="166" width="220" height="148" rx="14" stroke="#E7E7F4" strokeWidth="1" />
              <rect x="152" y="180" width="90"  height="11" rx="5.5" fill="#1E1E2F" opacity="0.6" />
              <rect x="152" y="196" width="56"  height="7"  rx="3.5" fill="#C4B5FD" opacity="0.5" />
              {/* bars */}
              {[
                { x: 152, h: 56, c: '#6D5DF6' },
                { x: 174, h: 38, c: '#A855F7' },
                { x: 196, h: 68, c: '#6D5DF6' },
                { x: 218, h: 46, c: '#A855F7' },
                { x: 240, h: 60, c: '#6D5DF6' },
                { x: 262, h: 32, c: '#C4B5FD' },
                { x: 284, h: 52, c: '#6D5DF6' },
                { x: 306, h: 72, c: '#A855F7' },
              ].map(({ x, h, c }, i) => (
                <rect key={i} x={x} y={298 - h} width="16" height={h} rx="5" fill={c} opacity="0.72" />
              ))}
              <line x1="152" y1="298" x2="328" y2="298" stroke="#E7E7F4" strokeWidth="1" />

              {/* ── Orders list card ── */}
              <rect x="368" y="166" width="160" height="148" rx="14" fill="white" />
              <rect x="368" y="166" width="160" height="148" rx="14" stroke="#E7E7F4" strokeWidth="1" />
              <rect x="382" y="180" width="80"  height="10" rx="5"   fill="#1E1E2F" opacity="0.6" />
              {[204, 226, 248, 270, 292].map((y, i) => (
                <g key={i}>
                  <circle cx="390" cy={y} r="6" fill={['#6D5DF6','#10B981','#F59E0B','#EF4444','#A855F7'][i]} opacity="0.65" />
                  <rect x="402" y={y - 4} width={44 + (i % 3) * 8} height="8" rx="4" fill="#F0EEFF" />
                  <rect x={456 + (i % 2) * 4} y={y - 4} width="32" height="8" rx="4"
                    fill={['#EDE9FE','#ECFDF5','#FEF3C7','#FEE2E2','#F3E8FF'][i]} />
                </g>
              ))}

              {/* ── Floating badge: cart ── */}
              <g transform="translate(462, 22)">
                <rect width="40" height="40" rx="11" fill="white" />
                <rect width="40" height="40" rx="11" stroke="#E7E7F4" strokeWidth="1" />
                <path d="M10 12h3l3 10h12l2.5-7H13" stroke="#6D5DF6" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" fill="none" />
                <circle cx="17" cy="26" r="2" fill="#6D5DF6" />
                <circle cx="26" cy="26" r="2" fill="#6D5DF6" />
              </g>

              {/* ── Floating badge: shield ── */}
              <g transform="translate(22, 308)">
                <rect width="38" height="38" rx="10" fill="white" />
                <rect width="38" height="38" rx="10" stroke="#E7E7F4" strokeWidth="1" />
                <path d="M19 7 L28 10.5 L28 18 C28 22.5 24 26 19 28 C14 26 10 22.5 10 18 L10 10.5 Z"
                  stroke="#A855F7" strokeWidth="1.6" fill="none" strokeLinejoin="round" />
                <path d="M14.5 19 L17.5 22 L23.5 15" stroke="#A855F7" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
              </g>

              {/* ── Approval chip ── */}
              <g transform="translate(136, 326)">
                <rect width="110" height="26" rx="13" fill="#ECFDF5" />
                <circle cx="18" cy="13" r="6" fill="#10B981" opacity="0.8" />
                <path d="M15 13 L17 15 L21 11" stroke="white" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
                <rect x="30" y="8" width="68" height="10" rx="5" fill="#10B981" opacity="0.25" />
              </g>

              <defs>
                <linearGradient id="sbGrad" x1="0" y1="0" x2="1" y2="0">
                  <stop offset="0%"   stopColor="#EDE9FE" stopOpacity="0.7" />
                  <stop offset="100%" stopColor="#F5F3FF" stopOpacity="0"   />
                </linearGradient>
              </defs>
            </svg>
          </motion.div>

          {/* Feature rows */}
          <motion.div className="auth-feature-list" variants={stagger}>
            {FEATURES.map(({ icon, label, desc }) => (
              <motion.div className="auth-feature-row" key={label} variants={fadeUp}>
                <div className="auth-feature-icon-wrap">
                  <i className={`bi ${icon}`} />
                </div>
                <div className="auth-feature-text">
                  <span className="auth-feature-title">{label}</span>
                  <span className="auth-feature-desc">{desc}</span>
                </div>
              </motion.div>
            ))}
          </motion.div>

          {/* Footer */}
          <motion.div className="auth-left-footer" variants={fadeUp}>
            <span>v{APP_VERSION}</span>
            <span className="auth-footer-dot" />
            <span>{COPYRIGHT}</span>
          </motion.div>

        </motion.div>
      </motion.div>

      {/* ── RIGHT PANEL 45% ── */}
      <motion.div className="auth-right-panel" initial="hidden" animate="visible" variants={formV}>
        <div className="auth-mobile-logo">
          <Logo size="sm" />
        </div>

        <div className="auth-right-inner">
          <div className="auth-form-card">
            {children}
          </div>
        </div>

        <div className="auth-right-footer">
          <span>v{APP_VERSION}</span>
          <span className="auth-footer-dot" />
          <span>{COPYRIGHT}</span>
        </div>
      </motion.div>

    </div>
  );
};

export const AuthDivider = ({ text = 'or' }) => (
  <div className="auth-divider">
    <span className="auth-divider-line" />
    <span className="auth-divider-text">{text}</span>
    <span className="auth-divider-line" />
  </div>
);

export const AuthLink = ({ to, children, className = '' }) => (
  <Link to={to} className={`auth-link ${className}`}>
    {children}
  </Link>
);

export default AuthLayout;
