import { useState, useMemo } from 'react';

/**
 * Enterprise Data Table Component.
 * Supports live search, dropdown filtering, column sorting, pagination, empty states, and custom action cells.
 */
const DataTable = ({
  columns = [],
  data = [],
  searchPlaceholder = 'Search records...',
  filterKey = null,
  filterOptions = [],
  onRowClick = null,
  pageSize = 6,
  emptyMessage = 'No records found matching criteria',
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterValue, setFilterValue] = useState('ALL');
  const [sortColumn, setSortColumn] = useState(null);
  const [sortDirection, setSortDirection] = useState('asc');
  const [currentPage, setCurrentPage] = useState(1);

  // Search & Filter
  const filteredData = useMemo(() => {
    return data.filter((item) => {
      // Dropdown filter
      if (filterKey && filterValue !== 'ALL') {
        const itemVal = (item[filterKey] || '').toString().toLowerCase();
        if (itemVal !== filterValue.toLowerCase()) return false;
      }

      // Search term
      if (searchTerm.trim()) {
        const term = searchTerm.toLowerCase();
        return Object.values(item).some((val) =>
          (val || '').toString().toLowerCase().includes(term)
        );
      }

      return true;
    });
  }, [data, filterKey, filterValue, searchTerm]);

  // Sort
  const sortedData = useMemo(() => {
    if (!sortColumn) return filteredData;

    return [...filteredData].sort((a, b) => {
      let valA = a[sortColumn];
      let valB = b[sortColumn];

      if (typeof valA === 'number' && typeof valB === 'number') {
        return sortDirection === 'asc' ? valA - valB : valB - valA;
      }

      valA = (valA || '').toString().toLowerCase();
      valB = (valB || '').toString().toLowerCase();

      if (valA < valB) return sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredData, sortColumn, sortDirection]);

  // Pagination
  const totalPages = Math.ceil(sortedData.length / pageSize) || 1;
  const paginatedData = useMemo(() => {
    const start = (currentPage - 1) * pageSize;
    return sortedData.slice(start, start + pageSize);
  }, [sortedData, currentPage, pageSize]);

  const handleSort = (key) => {
    if (sortColumn === key) {
      setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortColumn(key);
      setSortDirection('asc');
    }
  };

  return (
    <div className="finance-table-wrapper card border-0 shadow-sm">
      {/* Top Filter & Search Controls */}
      <div className="card-header bg-transparent border-0 p-3 pb-0">
        <div className="row g-2 align-items-center justify-content-between">
          <div className="col-12 col-md-6 col-lg-5">
            <div className="input-group input-group-sm">
              <span className="input-group-text bg-light border-end-0">
                <i className="bi bi-search text-muted" />
              </span>
              <input
                type="text"
                className="form-control border-start-0 bg-light"
                placeholder={searchPlaceholder}
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(1);
                }}
              />
              {searchTerm && (
                <button
                  type="button"
                  className="btn btn-outline-secondary border-start-0"
                  onClick={() => setSearchTerm('')}
                >
                  <i className="bi bi-x" />
                </button>
              )}
            </div>
          </div>

          {filterKey && filterOptions.length > 0 && (
            <div className="col-12 col-md-4 col-lg-3 text-md-end">
              <select
                className="form-select form-select-sm"
                value={filterValue}
                onChange={(e) => {
                  setFilterValue(e.target.value);
                  setCurrentPage(1);
                }}
              >
                <option value="ALL">All Categories / Statuses</option>
                {filterOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
      </div>

      {/* Table Body */}
      <div className="table-responsive mt-3">
        <table className="table table-hover align-middle mb-0 text-nowrap">
          <thead className="table-light">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key || col.header}
                  onClick={() => col.sortable !== false && handleSort(col.key)}
                  style={{
                    cursor: col.sortable !== false ? 'pointer' : 'default',
                    userSelect: 'none',
                  }}
                  className="small text-uppercase text-secondary fw-semibold py-3"
                >
                  <div className="d-flex align-items-center gap-1">
                    <span>{col.header}</span>
                    {col.sortable !== false && sortColumn === col.key && (
                      <i className={`bi bi-arrow-${sortDirection === 'asc' ? 'up' : 'down'} text-primary`} />
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {paginatedData.length > 0 ? (
              paginatedData.map((row, idx) => (
                <tr
                  key={row.id || idx}
                  onClick={() => onRowClick?.(row)}
                  style={{ cursor: onRowClick ? 'pointer' : 'default' }}
                >
                  {columns.map((col) => (
                    <td key={col.key || col.header} className="py-3">
                      {col.render ? col.render(row) : row[col.key] ?? '—'}
                    </td>
                  ))}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={columns.length} className="text-center py-5 text-muted">
                  <i className="bi bi-inbox fs-2 text-secondary d-block mb-2 opacity-50" />
                  <span>{emptyMessage}</span>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Footer Pagination Controls */}
      <div className="card-footer bg-transparent border-0 d-flex flex-wrap align-items-center justify-content-between p-3 pt-2">
        <span className="small text-muted mb-2 mb-sm-0">
          Showing {sortedData.length > 0 ? (currentPage - 1) * pageSize + 1 : 0} to{' '}
          {Math.min(currentPage * pageSize, sortedData.length)} of {sortedData.length} records
        </span>

        {totalPages > 1 && (
          <nav aria-label="Table pagination">
            <ul className="pagination pagination-sm mb-0">
              <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                <button
                  className="page-item-link btn btn-sm btn-outline-secondary me-1"
                  onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
                  disabled={currentPage === 1}
                >
                  Previous
                </button>
              </li>
              {[...Array(totalPages)].map((_, i) => (
                <li key={i} className="page-item">
                  <button
                    className={`btn btn-sm me-1 ${
                      currentPage === i + 1 ? 'btn-primary' : 'btn-outline-secondary'
                    }`}
                    onClick={() => setCurrentPage(i + 1)}
                  >
                    {i + 1}
                  </button>
                </li>
              ))}
              <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
                <button
                  className="page-item-link btn btn-sm btn-outline-secondary"
                  onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
                  disabled={currentPage === totalPages}
                >
                  Next
                </button>
              </li>
            </ul>
          </nav>
        )}
      </div>
    </div>
  );
};

export default DataTable;
