/**
 * Extracts a user-facing message from Axios / Spring Boot error responses.
 */
export const getApiErrorMessage = (error, fallback = 'Login failed. Please verify your credentials and try again.') => {
  if (!error) return fallback;

  const data = error.response?.data;

  if (typeof data === 'string' && data.trim()) {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  if (Array.isArray(data?.errors) && data.errors.length > 0) {
    return data.errors
      .map((entry) => entry.defaultMessage || entry.message || entry)
      .filter(Boolean)
      .join(' ');
  }

  if (data?.errors && typeof data.errors === 'object' && !Array.isArray(data.errors)) {
    const messages = Object.values(data.errors).flat().filter(Boolean);
    if (messages.length > 0) {
      return messages.join(' ');
    }
  }

  if (data?.error) {
    return data.error;
  }

  if (error.code === 'ERR_NETWORK') {
    return 'Unable to connect to the server. Please check your network and try again.';
  }

  if (error.message && !error.message.startsWith('Request failed with status code')) {
    return error.message;
  }

  return fallback;
};
