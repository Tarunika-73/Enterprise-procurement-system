import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import PurchaseRequestForm from '../../components/employee/PurchaseRequestForm';
import Toast from '../../components/Authentication/Toast/Toast';
import { getEmployeeProductCatalog } from '../../services/productService';
import {
  createPurchaseRequest,
  getAssignmentPreview,
  getPurchaseRequestById,
  updatePurchaseRequest,
} from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { unwrapApiData } from '../../utils/employeeHelpers';

const CreatePurchaseRequestPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const initialProductId = searchParams.get('productId') || '';
  const editRequestId = searchParams.get('edit');

  const [products, setProducts] = useState([]);
  const [assignmentPreview, setAssignmentPreview] = useState(null);
  const [initialValues, setInitialValues] = useState(null);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  useEffect(() => {
    let mounted = true;

    const loadData = async () => {
      setLoadingProducts(true);
      try {
        const [catalogResponse, previewResponse, requestResponse] = await Promise.all([
          getEmployeeProductCatalog(),
          getAssignmentPreview(),
          editRequestId ? getPurchaseRequestById(editRequestId) : Promise.resolve(null),
        ]);
        if (!mounted) return;

        const catalog = unwrapApiData(catalogResponse);
        const combined = [
          ...(catalog?.departmentProducts || []),
          ...(catalog?.otherDepartmentProducts || []),
        ];
        setProducts(combined);
        setAssignmentPreview(unwrapApiData(previewResponse));
        if (requestResponse) {
          const request = unwrapApiData(requestResponse);
          if (request?.status !== 'PENDING') {
            throw new Error('Only requests awaiting manager approval can be edited.');
          }
          setInitialValues({
            ...request,
            expectedDeliveryDate: request.expectedDeliveryDate?.slice(0, 10) || '',
          });
        }
      } catch (err) {
        if (mounted) {
          setToast({
            show: true,
            message: getApiErrorMessage(err, 'Unable to load purchase request form.'),
            type: 'danger',
          });
        }
      } finally {
        if (mounted) setLoadingProducts(false);
      }
    };

    loadData();
    return () => {
      mounted = false;
    };
  }, [editRequestId]);

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      if (editRequestId) {
        await updatePurchaseRequest(editRequestId, payload);
      } else {
        await createPurchaseRequest(payload);
      }
      setToast({
        show: true,
        message: editRequestId ? 'Purchase request updated successfully.' : 'Purchase request submitted and assigned to your department manager.',
        type: 'success',
      });
      setTimeout(() => {
        navigate('/employee/purchase-requests', { replace: true });
      }, 700);
    } catch (err) {
      setToast({
        show: true,
        message: getApiErrorMessage(err, editRequestId ? 'Failed to update purchase request.' : 'Failed to create purchase request.'),
        type: 'danger',
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Toast
        show={toast.show}
        message={toast.message}
        type={toast.type}
        onClose={() => setToast((prev) => ({ ...prev, show: false }))}
      />

      <div className="dashboard-page-header">
          <h1>{editRequestId ? 'Edit Purchase Request' : 'Create Purchase Request'}</h1>
        <p className="text-muted mb-0">
          Your department manager is assigned automatically. You cannot change the manager.
        </p>
      </div>

      {loadingProducts ? (
        <div className="employee-form-card text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      ) : (
        <PurchaseRequestForm
          products={products}
          initialProductId={initialProductId}
          assignmentPreview={assignmentPreview}
          initialValues={initialValues}
          submitting={submitting}
          submitLabel={editRequestId ? 'Save Changes' : 'Submit Request'}
          onSubmit={handleSubmit}
          onCancel={() => navigate(-1)}
        />
      )}
    </>
  );
};

export default CreatePurchaseRequestPage;
