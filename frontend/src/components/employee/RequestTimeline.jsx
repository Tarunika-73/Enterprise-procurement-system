import { formatDateTime, formatStatusLabel, getStatusBadgeClass } from '../../utils/employeeHelpers';

const RequestTimeline = ({ timeline = [] }) => {
  if (!timeline.length) {
    return <p className="text-muted mb-0">No approval timeline available yet.</p>;
  }

  return (
    <ul className="employee-timeline list-unstyled mb-0">
      {timeline.map((entry, index) => (
        <li key={`${entry.stage}-${index}`} className="employee-timeline-item">
          <div className={`employee-timeline-dot bg-${getStatusBadgeClass(entry.status)}`} />
          <div className="employee-timeline-content">
            <div className="d-flex flex-wrap justify-content-between gap-2">
              <strong>{entry.stage}</strong>
              <span className={`badge text-bg-${getStatusBadgeClass(entry.status)}`}>
                {formatStatusLabel(entry.status)}
              </span>
            </div>
            <div className="text-muted small mt-1">
              {entry.actorName || 'System'} · {formatDateTime(entry.timestamp)}
            </div>
            {entry.remarks ? <p className="mb-0 mt-2">{entry.remarks}</p> : null}
          </div>
        </li>
      ))}
    </ul>
  );
};

export default RequestTimeline;
