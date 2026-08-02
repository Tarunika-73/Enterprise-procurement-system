import api from './api';

export const getMyNotifications = async ({
  page = 0,
  size = 20,
  sort = 'createdAt,desc',
} = {}) => {
  const response = await api.get('/v1/notifications/my', {
    params: { page, size, sort },
  });
  return response.data;
};

export default { getMyNotifications };
