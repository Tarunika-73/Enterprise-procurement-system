import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import ProductTable from '../../components/employee/ProductTable';
import { getCategories, getEmployeeProductCatalog } from '../../services/productService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { getPageContent, unwrapApiData } from '../../utils/employeeHelpers';

const ProductListPage = () => {
  const { user } = useAuth();
  const [departmentProducts, setDepartmentProducts] = useState([]);
  const [otherProducts, setOtherProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [searchInput, setSearchInput] = useState('');
  const [search, setSearch] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const response = await getCategories();
        setCategories(getPageContent(response));
      } catch {
        setCategories([]);
      }
    };
    loadCategories();
  }, []);

  const loadProducts = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await getEmployeeProductCatalog({
        search,
        categoryId: categoryId || undefined,
      });
      const data = unwrapApiData(response);
      setDepartmentProducts(data?.departmentProducts || []);
      setOtherProducts(data?.otherDepartmentProducts || []);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load products.'));
      setDepartmentProducts([]);
      setOtherProducts([]);
    } finally {
      setLoading(false);
    }
  }, [search, categoryId]);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  const handleSearchSubmit = (event) => {
    event.preventDefault();
    setSearch(searchInput.trim());
  };

  return (
    <>
      <div className="dashboard-page-header">
        <h1>Product Catalog</h1>
        <p className="text-muted mb-0">
          Products for your department ({user?.departmentName || 'N/A'}) appear first.
        </p>
      </div>

      <form className="employee-filter-bar row g-3 mb-4" onSubmit={handleSearchSubmit}>
        <div className="col-md-6">
          <label className="form-label" htmlFor="product-search">
            Search Products
          </label>
          <input
            id="product-search"
            className="form-control"
            placeholder="Search by name or SKU"
            value={searchInput}
            onChange={(event) => setSearchInput(event.target.value)}
          />
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="product-category">
            Category
          </label>
          <select
            id="product-category"
            className="form-select"
            value={categoryId}
            onChange={(event) => setCategoryId(event.target.value)}
          >
            <option value="">All Categories</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </div>
        <div className="col-md-2 d-flex align-items-end">
          <button type="submit" className="btn btn-primary w-100">
            Search
          </button>
        </div>
      </form>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <div className="mb-4">
        <h2 className="h5 mb-3">Products For Your Department</h2>
        <ProductTable
          products={departmentProducts}
          loading={loading}
          emptyMessage="No products found for your department."
        />
      </div>

      <div>
        <h2 className="h5 mb-3">Products From Other Departments</h2>
        <ProductTable
          products={otherProducts}
          loading={loading}
          emptyMessage="No products from other departments."
        />
      </div>
    </>
  );
};

export default ProductListPage;
