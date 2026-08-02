import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import PurchaseRequestForm from '../../components/employee/PurchaseRequestForm';
import Toast from '../../components/Authentication/Toast/Toast';
import { getEmployeeProductCatalog } from '../../services/productService';
import {
  createPurchaseRequest,
  getAssignmentPreview,
} from '../../services/purchaseRequestService';
import { getApiErrorMessage } from '../../utils/apiErrors';
import { unwrapApiData } from '../../utils/employeeHelpers';

const CreatePurchaseRequestPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const initialProductId = searchParams.get('productId') || '';

  const [products, setProducts] = useState([]);
  const [assignmentPreview, setAssignmentPreview] = useState(null);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  useEffect(() => {
    let mounted = true;

    const loadData = async () => {
      setLoadingProducts(true);
      try {
        const [catalogResponse, previewResponse] = await Promise.all([
          getEmployeeProductCatalog(),
          getAssignmentPreview(),
        ]);
        if (!mounted) return;

        const catalog = unwrapApiData(catalogResponse);
        const combined = [
          ...(catalog?.departmentProducts || []),
          ...(catalog?.otherDepartmentProducts || []),
        ];
        setProducts(combined);
        setAssignmentPreview(unwrapApiData(previewResponse));
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
  }, []);

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      await createPurchaseRequest(payload);
      setToast({
        show: true,
        message: 'Purchase request submitted and assigned to your department manager.',
        type: 'success',
      });
      setTimeout(() => {
        navigate('/employee/purchase-requests', { replace: true });
      }, 700);
    } catch (err) {
      setToast({
        show: true,
        message: getApiErrorMessage(err, 'Failed to create purchase request.'),
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
        <h1>Create Purchase Request</h1>
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
          submitting={submitting}
          onSubmit={handleSubmit}
          onCancel={() => navigate(-1)}
        />
      )}
    </>
  );
};

export default CreatePurchaseRequestPage;
