import { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getDisplayName } from '../../utils/userDisplay';
import { formatCurrency, formatDate, REQUEST_PRIORITIES } from '../../utils/employeeHelpers';
import { validatePurchaseRequestForm } from '../../utils/purchaseRequestValidation';

const PurchaseRequestForm = ({
  products = [],
  initialProductId = '',
  assignmentPreview = null,
  initialValues = null,
  submitLabel = 'Submit Request',
  submitting = false,
  onSubmit,
  onCancel,
  onProductChange,
}) => {
  const { user } = useAuth();
  const [values, setValues] = useState({
    productId: initialProductId ? String(initialProductId) : '',
    quantity: 1,
    title: '',
    justification: '',
    expectedDeliveryDate: '',
    priority: 'NORMAL',
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialProductId) {
      setValues((prev) => ({ ...prev, productId: String(initialProductId) }));
    }
  }, [initialProductId]);

  useEffect(() => {
    if (!initialValues) return;
    setValues((prev) => ({
      ...prev,
      productId: initialValues.productId ? String(initialValues.productId) : prev.productId,
      quantity: initialValues.quantity ?? prev.quantity,
      title: initialValues.title ?? prev.title,
      justification: initialValues.justification ?? prev.justification,
      expectedDeliveryDate: initialValues.expectedDeliveryDate ?? prev.expectedDeliveryDate,
      priority: initialValues.priority ?? prev.priority,
    }));
  }, [initialValues]);

  const selectedProduct = useMemo(
    () => products.find((product) => String(product.id) === String(values.productId)),
    [products, values.productId]
  );

  const unitPrice = Number(selectedProduct?.price ?? 0);
  const totalAmount = unitPrice * Number(values.quantity || 0);

  const employeeName = assignmentPreview?.requesterName || getDisplayName(user);
  const employeeCode = assignmentPreview?.employeeCode || user?.employeeId || '—';
  const departmentName = assignmentPreview?.departmentName || user?.departmentName || '—';
  const managerName = assignmentPreview?.managerName || 'Not assigned';
  const requestDate = formatDate(assignmentPreview?.createdAt || new Date().toISOString());

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({
      ...prev,
      [name]: name === 'quantity' && Number(value) > 100 ? 'Quantity cannot exceed 100.' : undefined,
    }));
    if (name === 'productId') onProductChange?.(value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const nextErrors = validatePurchaseRequestForm(values);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    onSubmit?.({
      productId: Number(values.productId),
      quantity: Number(values.quantity),
      unitPrice,
      title: values.title.trim(),
      justification: values.justification.trim(),
      expectedDeliveryDate: values.expectedDeliveryDate,
      priority: values.priority,
    });
  };

  return (
    <form className="employee-form-card" onSubmit={handleSubmit} noValidate>
      <div className="row g-3 mb-1">
        <div className="col-md-3">
          <label className="form-label" htmlFor="employeeName">
            Employee Name
          </label>
          <input id="employeeName" className="form-control" value={employeeName} readOnly />
        </div>
        <div className="col-md-3">
          <label className="form-label" htmlFor="employeeCode">
            Employee ID
          </label>
          <input id="employeeCode" className="form-control" value={employeeCode} readOnly />
        </div>
        <div className="col-md-3">
          <label className="form-label" htmlFor="department">
            Employee Department
          </label>
          <input id="department" className="form-control" value={departmentName} readOnly />
        </div>
        <div className="col-md-3">
          <label className="form-label" htmlFor="manager">
            Manager
          </label>
          <input id="manager" className="form-control" value={managerName} readOnly />
        </div>
        <div className="col-md-3">
          <label className="form-label" htmlFor="requestDate">
            Current Date
          </label>
          <input id="requestDate" className="form-control" value={requestDate} readOnly />
        </div>
      </div>

      <hr className="my-3" />

      <div className="row g-3">
        <div className="col-md-6">
          <label className="form-label" htmlFor="productId">
            Product
          </label>
          <select
            id="productId"
            name="productId"
            className={`form-select ${errors.productId ? 'is-invalid' : ''}`}
            value={values.productId}
            onChange={handleChange}
          >
            <option value="">Select a product</option>
            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.name} ({product.sku})
                {product.departmentName ? ` — ${product.departmentName}` : ''}
              </option>
            ))}
          </select>
          {errors.productId ? <div className="invalid-feedback">{errors.productId}</div> : null}
        </div>

        <div className="col-md-3">
          <label className="form-label" htmlFor="quantity">
            Quantity
          </label>
          <input
            id="quantity"
            name="quantity"
            type="number"
            min="1"
            className={`form-control ${errors.quantity ? 'is-invalid' : ''}`}
            value={values.quantity}
            onChange={handleChange}
          />
          {errors.quantity ? <div className="invalid-feedback">{errors.quantity}</div> : null}
        </div>

        <div className="col-md-3">
          <label className="form-label" htmlFor="unitPrice">
            Unit Price
          </label>
          <input
            id="unitPrice"
            className="form-control"
            value={selectedProduct ? formatCurrency(unitPrice) : ''}
            readOnly
          />
        </div>

        <div className="col-md-4">
          <label className="form-label" htmlFor="totalAmount">
            Total Amount
          </label>
          <input
            id="totalAmount"
            className="form-control"
            value={selectedProduct ? formatCurrency(totalAmount) : ''}
            readOnly
          />
        </div>

        <div className="col-md-8">
          <label className="form-label" htmlFor="title">
            Request Title
          </label>
          <input
            id="title"
            name="title"
            type="text"
            maxLength={200}
            className={`form-control ${errors.title ? 'is-invalid' : ''}`}
            value={values.title}
            onChange={handleChange}
            placeholder="e.g. Laptop for new joiner"
          />
          {errors.title ? <div className="invalid-feedback">{errors.title}</div> : null}
        </div>

        <div className="col-12">
          <label className="form-label" htmlFor="justification">
            Business Justification
          </label>
          <textarea
            id="justification"
            name="justification"
            rows={4}
            className={`form-control ${errors.justification ? 'is-invalid' : ''}`}
            value={values.justification}
            onChange={handleChange}
            placeholder="Explain why this purchase is needed (minimum 20 characters)"
          />
          {errors.justification ? (
            <div className="invalid-feedback">{errors.justification}</div>
          ) : null}
        </div>

        <div className="col-md-4">
          <label className="form-label" htmlFor="expectedDeliveryDate">
            Expected Delivery Date
          </label>
          <input
            id="expectedDeliveryDate"
            name="expectedDeliveryDate"
            type="date"
            className={`form-control ${errors.expectedDeliveryDate ? 'is-invalid' : ''}`}
            value={values.expectedDeliveryDate}
            onChange={handleChange}
          />
          {errors.expectedDeliveryDate ? (
            <div className="invalid-feedback">{errors.expectedDeliveryDate}</div>
          ) : null}
        </div>

        <div className="col-md-4">
          <label className="form-label" htmlFor="priority">
            Priority
          </label>
          <select
            id="priority"
            name="priority"
            className={`form-select ${errors.priority ? 'is-invalid' : ''}`}
            value={values.priority}
            onChange={handleChange}
          >
            {REQUEST_PRIORITIES.map((priority) => (
              <option key={priority.value} value={priority.value}>
                {priority.label}
              </option>
            ))}
          </select>
          {errors.priority ? <div className="invalid-feedback">{errors.priority}</div> : null}
        </div>
      </div>

      <div className="d-flex flex-wrap gap-2 mt-4">
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving...' : submitLabel}
        </button>
        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={onCancel}
          disabled={submitting}
        >
          Cancel
        </button>
      </div>
    </form>
  );
};

export default PurchaseRequestForm;
