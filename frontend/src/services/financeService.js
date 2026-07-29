import api from './api';
import {
  MOCK_FINANCE_STATS,
  MOCK_MONTHLY_SPENDING,
  MOCK_VENDOR_SPENDING,
  MOCK_DEPARTMENT_SPENDING,
  MOCK_INVOICE_STATUS_DISTRIBUTION,
  MOCK_PAYMENT_STATUS_DISTRIBUTION,
  MOCK_PURCHASE_ORDERS,
  MOCK_INVOICES,
  MOCK_PAYMENTS,
  MOCK_VENDOR_PAYMENT_HISTORY,
  MOCK_AUDIT_LOGS,
  MOCK_NOTIFICATIONS,
} from '../utils/mockFinanceData';

/**
 * Service handling all REST API calls for the Finance Module.
 * Integrates with Spring Boot backend endpoints and falls back to mock data if unfulfilled.
 */

// Helper to simulate slight async delay for smooth UI transition
const asyncFallback = (data, delay = 150) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay));

export const financeService = {
  /** Fetch overview metrics & chart data for Finance Dashboard */
  async getDashboardMetrics() {
    try {
      const response = await api.get('/finance/dashboard');
      return response.data;
    } catch {
      return asyncFallback({
        stats: MOCK_FINANCE_STATS,
        monthlySpending: MOCK_MONTHLY_SPENDING,
        vendorSpending: MOCK_VENDOR_SPENDING,
        departmentSpending: MOCK_DEPARTMENT_SPENDING,
        invoiceStatus: MOCK_INVOICE_STATUS_DISTRIBUTION,
        paymentStatus: MOCK_PAYMENT_STATUS_DISTRIBUTION,
      });
    }
  },

  /** Fetch Purchase Orders list */
  async getPurchaseOrders() {
    try {
      const response = await api.get('/purchase-orders');
      return response.data;
    } catch {
      return asyncFallback(MOCK_PURCHASE_ORDERS);
    }
  },

  /** Fetch Purchase Order details by ID */
  async getPurchaseOrderById(id) {
    try {
      const response = await api.get(`/purchase-orders/${id}`);
      return response.data;
    } catch {
      const po = MOCK_PURCHASE_ORDERS.find((p) => p.id === id || p.poNumber === id);
      return asyncFallback(po || MOCK_PURCHASE_ORDERS[0]);
    }
  },

  /** Fetch Invoices list */
  async getInvoices() {
    try {
      const response = await api.get('/invoices');
      return response.data;
    } catch {
      return asyncFallback(MOCK_INVOICES);
    }
  },

  /** Create a new Invoice */
  async createInvoice(invoiceData) {
    try {
      const response = await api.post('/invoices', invoiceData);
      return response.data;
    } catch {
      const newInvoice = {
        id: `INV-2026-${Math.floor(100 + Math.random() * 900)}`,
        status: 'Pending Approval',
        paymentStatus: 'Unpaid',
        tax: Number(invoiceData.tax || 0),
        discount: Number(invoiceData.discount || 0),
        total: Number(invoiceData.amount || 0) + Number(invoiceData.tax || 0) - Number(invoiceData.discount || 0),
        attachments: invoiceData.file ? [{ name: invoiceData.file.name, size: 'Original File', url: '#' }] : [],
        ...invoiceData,
      };
      return asyncFallback(newInvoice);
    }
  },

  /** Update an existing Invoice */
  async updateInvoice(id, updateData) {
    try {
      const response = await api.put(`/invoices/${id}`, updateData);
      return response.data;
    } catch {
      return asyncFallback({ id, ...updateData });
    }
  },

  /** Delete an Invoice */
  async deleteInvoice(id) {
    try {
      await api.delete(`/invoices/${id}`);
      return true;
    } catch {
      return asyncFallback(true);
    }
  },

  /** Fetch Payments list */
  async getPayments() {
    try {
      const response = await api.get('/payments');
      return response.data;
    } catch {
      return asyncFallback(MOCK_PAYMENTS);
    }
  },

  /** Create a new Payment */
  async createPayment(paymentData) {
    try {
      const response = await api.post('/payments', paymentData);
      return response.data;
    } catch {
      const newPayment = {
        id: `PAY-2026-${Math.floor(8000 + Math.random() * 1000)}`,
        paymentId: `PAY-2026-${Math.floor(8000 + Math.random() * 1000)}`,
        paymentDate: new Date().toISOString().split('T')[0],
        status: 'Completed',
        ...paymentData,
      };
      return asyncFallback(newPayment);
    }
  },

  /** Update an existing Payment */
  async updatePayment(id, updateData) {
    try {
      const response = await api.put(`/payments/${id}`, updateData);
      return response.data;
    } catch {
      return asyncFallback({ id, ...updateData });
    }
  },

  /** Delete a Payment entry */
  async deletePayment(id) {
    try {
      await api.delete(`/payments/${id}`);
      return true;
    } catch {
      return asyncFallback(true);
    }
  },

  /** Fetch Vendor Payment summary and history */
  async getVendorPayments() {
    try {
      const response = await api.get('/vendor-payments');
      return response.data;
    } catch {
      return asyncFallback(MOCK_VENDOR_PAYMENT_HISTORY);
    }
  },

  /** Fetch Expense analytics for visual dashboards */
  async getExpenseReports() {
    try {
      const response = await api.get('/reports/expense');
      return response.data;
    } catch {
      return asyncFallback({
        monthly: MOCK_MONTHLY_SPENDING,
        vendor: MOCK_VENDOR_SPENDING,
        department: MOCK_DEPARTMENT_SPENDING,
        invoiceDistribution: MOCK_INVOICE_STATUS_DISTRIBUTION,
        paymentDistribution: MOCK_PAYMENT_STATUS_DISTRIBUTION,
      });
    }
  },

  /** Fetch Audit Logs */
  async getAuditLogs() {
    try {
      const response = await api.get('/audit');
      return response.data;
    } catch {
      return asyncFallback(MOCK_AUDIT_LOGS);
    }
  },

  /** Fetch Notifications */
  async getNotifications() {
    try {
      const response = await api.get('/notifications');
      return response.data;
    } catch {
      return asyncFallback(MOCK_NOTIFICATIONS);
    }
  },

  /** Mark notification as read */
  async markNotificationRead(id) {
    try {
      await api.put(`/notifications/read/${id}`);
      return true;
    } catch {
      return asyncFallback(true);
    }
  },
};

export default financeService;
