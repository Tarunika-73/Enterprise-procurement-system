import api from './api';

/**
 * Product catalog APIs for the employee procurement workflow.
 */
export const getProducts = async ({
  search = '',
  categoryId,
  departmentId,
  page = 0,
  size = 10,
  sort = 'name,asc',
} = {}) => {
  const params = { page, size, sort };
  if (search?.trim()) params.search = search.trim();
  if (categoryId) params.categoryId = categoryId;
  if (departmentId) params.departmentId = departmentId;

  const response = await api.get('/v1/products', { params });
  return response.data;
};

export const getEmployeeProductCatalog = async ({ search = '', categoryId } = {}) => {
  const params = {};
  if (search?.trim()) params.search = search.trim();
  if (categoryId) params.categoryId = categoryId;
  const response = await api.get('/v1/products/employee-catalog', { params });
  return response.data;
};

export const getProductById = async (id) => {
  const response = await api.get(`/v1/products/${id}`);
  return response.data;
};

export const getCategories = async ({ page = 0, size = 100 } = {}) => {
  const response = await api.get('/v1/categories', {
    params: { page, size, sort: 'name,asc' },
  });
  return response.data;
};

export default { getProducts, getEmployeeProductCatalog, getProductById, getCategories };
