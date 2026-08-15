/**
 * Validation for Create Purchase Request form.
 */

export const validatePurchaseRequestForm = (values) => {
  const errors = {};

  if (!values.productId) {
    errors.productId = 'Please select a product.';
  }

  const quantity = Number(values.quantity);
  if (!values.quantity && values.quantity !== 0) {
    errors.quantity = 'Quantity is required.';
  } else if (!Number.isFinite(quantity) || quantity <= 0) {
    errors.quantity = 'Quantity must be greater than 0.';
  } else if (!Number.isInteger(quantity)) {
    errors.quantity = 'Quantity must be a whole number.';
  } else if (quantity > 100) {
    errors.quantity = 'Quantity cannot exceed 100.';
  }

  if (!values.title?.trim()) {
    errors.title = 'Request title is required.';
  } else if (values.title.trim().length > 200) {
    errors.title = 'Title must not exceed 200 characters.';
  }

  if (!values.justification?.trim()) {
    errors.justification = 'Business justification is required.';
  } else if (values.justification.trim().length < 20) {
    errors.justification = 'Justification must be at least 20 characters.';
  }

  if (!values.expectedDeliveryDate) {
    errors.expectedDeliveryDate = 'Expected delivery date is required.';
  } else {
    const selected = new Date(values.expectedDeliveryDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (Number.isNaN(selected.getTime()) || selected < today) {
      errors.expectedDeliveryDate = 'Expected delivery date must be today or later.';
    }
  }

  if (!values.priority) {
    errors.priority = 'Priority is required.';
  }

  return errors;
};
