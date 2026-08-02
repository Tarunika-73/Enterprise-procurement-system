/**
 * Helpers for Spring Page responses and employee module formatting.
 */

export const unwrapApiData = (response) => response?.data ?? response;

export const getPageContent = (pagePayload) => {
  const data = unwrapApiData(pagePayload);
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.data?.content)) return data.data.content;
  return [];
};

export const getPageMeta = (pagePayload) => {
  const data = unwrapApiData(pagePayload);
  const page = data?.content !== undefined ? data : data?.data;
  return {
    totalElements: page?.totalElements ?? 0,
    totalPages: page?.totalPages ?? 0,
    number: page?.number ?? 0,
    size: page?.size ?? 10,
  };
};

export const formatCurrency = (value) => {
  const amount = Number(value ?? 0);
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(Number.isFinite(amount) ? amount : 0);
};

export const formatDate = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '—';
  return date.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
};

export const formatDateTime = (value) => {
  if (!value) return '—';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '—';
  return date.toLocaleString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const REQUEST_PRIORITIES = [
  { value: 'NORMAL', label: 'Normal' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'URGENT', label: 'Urgent' },
];

export const getStatusBadgeClass = (status) => {
  const key = String(status || '').toUpperCase();
  if (key === 'APPROVED' || key === 'ACTIVE' || key === 'COMPLETED') return 'success';
  if (key === 'PENDING' || key === 'SUBMITTED' || key === 'MEDIUM') return 'warning';
  if (key === 'REJECTED' || key === 'INACTIVE' || key === 'OUT_OF_STOCK') return 'danger';
  if (key === 'HIGH' || key === 'URGENT') return 'danger';
  if (key === 'DRAFT' || key === 'NORMAL') return 'secondary';
  return 'primary';
};

export const formatStatusLabel = (status) => {
  if (!status) return '—';
  return String(status)
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
};
