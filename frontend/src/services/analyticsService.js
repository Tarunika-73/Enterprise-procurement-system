import api from './api';

export const getDashboardAnalytics = () =>
  api.get('/v1/analytics/dashboard').then((response) => response.data);
