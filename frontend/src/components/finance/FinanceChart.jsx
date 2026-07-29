import { useState } from 'react';

/**
 * Enterprise Chart Component supporting Bar, Line, Pie, and Area chart visualizations.
 * Built using responsive, clean SVG graphics for high performance and zero external bundle bloat.
 */
const FinanceChart = ({
  type = 'bar',
  data = [],
  title = '',
  height = 260,
  valuePrefix = '$',
  color = '#2563eb',
}) => {
  const [hoveredIndex, setHoveredIndex] = useState(null);

  if (!data || data.length === 0) {
    return (
      <div className="card shadow-sm border-0 h-100 p-4 d-flex align-items-center justify-content-center text-muted">
        <i className="bi bi-bar-chart fs-1 mb-2 text-secondary opacity-50" />
        <span>No chart data available</span>
      </div>
    );
  }

  const maxValue = Math.max(...data.map((d) => d.value || 0), 1);
  const formatVal = (v) => `${valuePrefix}${Number(v).toLocaleString()}`;

  // --- Render Bar Chart ---
  if (type === 'bar') {
    const barWidth = 36;
    const gap = 20;
    const paddingLeft = 40;
    const chartWidth = Math.max(data.length * (barWidth + gap) + paddingLeft + 20, 320);

    return (
      <div className="finance-chart-container">
        {title && <h6 className="fw-semibold text-dark mb-3">{title}</h6>}
        <div className="table-responsive">
          <svg width="100%" height={height} viewBox={`0 0 ${chartWidth} ${height}`} preserveAspectRatio="xMinYMin meet">
            {/* Gridlines */}
            {[0, 0.25, 0.5, 0.75, 1].map((ratio, i) => {
              const y = (height - 40) * (1 - ratio) + 10;
              const val = Math.round(maxValue * ratio);
              return (
                <g key={i}>
                  <line x1={paddingLeft} y1={y} x2={chartWidth - 10} y2={y} stroke="var(--bs-gray-200, #e2e8f0)" strokeDasharray="3 3" />
                  <text x={paddingLeft - 8} y={y + 4} textAnchor="end" fontSize="10" fill="#64748b">
                    {val >= 1000 ? `${(val / 1000).toFixed(0)}k` : val}
                  </text>
                </g>
              );
            })}

            {/* Bars */}
            {data.map((d, index) => {
              const x = paddingLeft + index * (barWidth + gap);
              const barHeight = Math.max(((d.value || 0) / maxValue) * (height - 50), 4);
              const y = height - 30 - barHeight;
              const isHovered = hoveredIndex === index;

              return (
                <g
                  key={index}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onMouseLeave={() => setHoveredIndex(null)}
                  style={{ cursor: 'pointer' }}
                >
                  <rect
                    x={x}
                    y={y}
                    width={barWidth}
                    height={barHeight}
                    rx="4"
                    fill={d.color || (isHovered ? '#1d4ed8' : color)}
                    opacity={isHovered ? 1 : 0.85}
                    className="transition-all"
                  />
                  <text
                    x={x + barWidth / 2}
                    y={height - 10}
                    textAnchor="middle"
                    fontSize="11"
                    fontWeight={isHovered ? '600' : '400'}
                    fill={isHovered ? '#1e293b' : '#64748b'}
                  >
                    {d.label}
                  </text>

                  {/* Tooltip on hover */}
                  {isHovered && (
                    <g>
                      <rect
                        x={x - 15}
                        y={Math.max(y - 30, 5)}
                        width={barWidth + 30}
                        height="22"
                        rx="4"
                        fill="#0f172a"
                      />
                      <text
                        x={x + barWidth / 2}
                        y={Math.max(y - 15, 20)}
                        textAnchor="middle"
                        fontSize="10"
                        fontWeight="bold"
                        fill="#ffffff"
                      >
                        {formatVal(d.value)}
                      </text>
                    </g>
                  )}
                </g>
              );
            })}
          </svg>
        </div>
      </div>
    );
  }

  // --- Render Line / Area Chart ---
  if (type === 'line' || type === 'area') {
    const paddingLeft = 45;
    const paddingBottom = 30;
    const chartWidth = 500;
    const chartHeight = height - paddingBottom - 10;
    const stepX = (chartWidth - paddingLeft - 20) / Math.max(data.length - 1, 1);

    const points = data.map((d, index) => {
      const x = paddingLeft + index * stepX;
      const y = chartHeight - ((d.value || 0) / maxValue) * (chartHeight - 20);
      return { x, y, ...d };
    });

    const pathString = points.reduce(
      (acc, p, i) => `${acc} ${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`,
      ''
    );

    const areaString = `${pathString} L ${points[points.length - 1].x} ${chartHeight} L ${points[0].x} ${chartHeight} Z`;

    return (
      <div className="finance-chart-container">
        {title && <h6 className="fw-semibold text-dark mb-3">{title}</h6>}
        <svg width="100%" height={height} viewBox={`0 0 ${chartWidth} ${height}`} preserveAspectRatio="xMinYMin meet">
          {/* Area fill */}
          {type === 'area' && (
            <path d={areaString} fill={color} opacity="0.15" />
          )}

          {/* Line */}
          <path d={pathString} fill="none" stroke={color} strokeWidth="3" strokeLinecap="round" />

          {/* Data Points */}
          {points.map((p, index) => {
            const isHovered = hoveredIndex === index;
            return (
              <g
                key={index}
                onMouseEnter={() => setHoveredIndex(index)}
                onMouseLeave={() => setHoveredIndex(null)}
                style={{ cursor: 'pointer' }}
              >
                <circle
                  cx={p.x}
                  cy={p.y}
                  r={isHovered ? 6 : 4}
                  fill={isHovered ? '#1d4ed8' : color}
                  stroke="#ffffff"
                  strokeWidth="2"
                />
                <text
                  x={p.x}
                  y={height - 10}
                  textAnchor="middle"
                  fontSize="11"
                  fill="#64748b"
                >
                  {p.label}
                </text>

                {isHovered && (
                  <g>
                    <rect x={p.x - 30} y={p.y - 32} width="60" height="22" rx="4" fill="#0f172a" />
                    <text x={p.x} y={p.y - 17} textAnchor="middle" fontSize="10" fontWeight="bold" fill="#fff">
                      {formatVal(p.value)}
                    </text>
                  </g>
                )}
              </g>
            );
          })}
        </svg>
      </div>
    );
  }

  // --- Render Pie / Donut Chart ---
  if (type === 'pie') {
    const total = data.reduce((acc, d) => acc + (d.value || 0), 0);
    const colors = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

    let currentAngle = 0;
    const slices = data.map((d, index) => {
      const value = d.value || 0;
      const angle = total > 0 ? (value / total) * 360 : 0;
      const startAngle = currentAngle;
      const endAngle = currentAngle + angle;
      currentAngle += angle;

      const x1 = 100 + 70 * Math.cos((Math.PI * (startAngle - 90)) / 180);
      const y1 = 100 + 70 * Math.sin((Math.PI * (startAngle - 90)) / 180);
      const x2 = 100 + 70 * Math.cos((Math.PI * (endAngle - 90)) / 180);
      const y2 = 100 + 70 * Math.sin((Math.PI * (endAngle - 90)) / 180);

      const largeArc = angle > 180 ? 1 : 0;
      const pathData = `M 100 100 L ${x1} ${y1} A 70 70 0 ${largeArc} 1 ${x2} ${y2} Z`;

      return {
        pathData,
        color: d.color || colors[index % colors.length],
        percentage: total > 0 ? ((value / total) * 100).toFixed(1) : 0,
        ...d,
      };
    });

    return (
      <div className="finance-chart-container">
        {title && <h6 className="fw-semibold text-dark mb-3">{title}</h6>}
        <div className="d-flex align-items-center flex-wrap justify-content-around gap-3">
          <svg width="200" height="200" viewBox="0 0 200 200">
            {slices.map((slice, index) => {
              const isHovered = hoveredIndex === index;
              return (
                <path
                  key={index}
                  d={slice.pathData}
                  fill={slice.color}
                  opacity={isHovered ? 1 : 0.85}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onMouseLeave={() => setHoveredIndex(null)}
                  style={{ cursor: 'pointer', transition: 'all 0.2s ease' }}
                  transform={isHovered ? 'scale(1.04)' : 'scale(1)'}
                  transform-origin="100 100"
                />
              );
            })}
            <circle cx="100" cy="100" r="42" fill="var(--bs-card-bg, #ffffff)" />
            <text x="100" y="98" textAnchor="middle" fontSize="11" fill="#64748b" fontWeight="600">
              Total
            </text>
            <text x="100" y="114" textAnchor="middle" fontSize="12" fill="#0f172a" fontWeight="bold">
              {formatVal(total)}
            </text>
          </svg>

          {/* Legend */}
          <div className="finance-chart-legend">
            {slices.map((s, index) => (
              <div
                key={index}
                className="d-flex align-items-center gap-2 mb-2"
                onMouseEnter={() => setHoveredIndex(index)}
                onMouseLeave={() => setHoveredIndex(null)}
                style={{ cursor: 'pointer' }}
              >
                <span
                  className="rounded-circle"
                  style={{ width: 10, height: 10, backgroundColor: s.color, display: 'inline-block' }}
                />
                <span className="small text-dark fw-medium">{s.label}</span>
                <span className="small text-muted ms-auto">{s.percentage}%</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return null;
};

export default FinanceChart;
