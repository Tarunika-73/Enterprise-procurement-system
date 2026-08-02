import api from './api';

export const getDepartments = async ({ page = 0, size = 50 } = {}) => {
  const response = await api.get('/v1/departments', {
    params: { page, size, sort: 'name,asc' },
  });
  return response.data;
};

export default { getDepartments };
