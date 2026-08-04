import api from './api';

export const getMyNotifications = async ({
  page = 0,
  size = 20,
  sort = 'createdAt,desc',
  isRead,
  type,
} = {}) => {
  const params = { page, size, sort };
  if (isRead !== undefined && isRead !== null) {
    params.isRead = isRead;
  }
  if (type) {
    params.type = type;
  }
  const response = await api.get('/v1/notifications/my', { params });
  return response.data;
};

export const getUnreadCount = async () => {
  const response = await api.get('/v1/notifications/unread-count');
  return response.data;
};

export const markAsRead = async (id) => {
  const response = await api.put(`/v1/notifications/${id}/read`);
  return response.data;
};

export const markAllAsRead = async () => {
  const response = await api.put('/v1/notifications/read-all');
  return response.data;
};

export const deleteNotification = async (id) => {
  const response = await api.delete(`/v1/notifications/${id}`);
  return response.data;
};

export default {
  getMyNotifications,
  getUnreadCount,
  markAsRead,
  markAllAsRead,
  deleteNotification,
};
