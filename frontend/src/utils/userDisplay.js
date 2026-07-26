import { normalizeRole } from './roleNavigation';

/**
 * Resolves the display name from backend or legacy user shapes.
 */
export const getDisplayName = (user) =>
  user?.name || user?.fullName || user?.email || 'User';

/**
 * Builds avatar initials from the resolved display name.
 */
export const getUserInitials = (user) => {
  const name = getDisplayName(user);

  if (name.includes('@')) {
    return name.charAt(0).toUpperCase();
  }

  return name
    .split(' ')
    .filter(Boolean)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();
};

/**
 * Formats backend role values (e.g. ADMIN) for UI display.
 */
export const formatRoleLabel = (role) => {
  const normalized = normalizeRole(role);
  if (normalized) return normalized;

  if (!role) return 'User';

  return role
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
};
