/**
 * Mock data generator for the Finance Module of Enterprise Procurement System.
 * Serves as fallback when backend Spring Boot API is not active or during development.
 */

export const MOCK_FINANCE_STATS = {
  totalPurchaseOrders: 148,
  pendingPurchaseOrders: 12,
  totalInvoices: 324,
  pendingInvoices: 28,
  paidInvoices: 285,
  pendingPayments: "$142,500",
  totalExpenses: "$3,450,000",
  monthlyExpenses: "$285,400",
  totalVendors: 42,
  overduePayments: 4,
};

export const MOCK_MONTHLY_SPENDING = [
  { label: 'Jan', value: 210000 },
  { label: 'Feb', value: 245000 },
  { label: 'Mar', value: 290000 },
  { label: 'Apr', value: 260000 },
  { label: 'May', value: 310000 },
  { label: 'Jun', value: 285400 },
  { label: 'Jul', value: 340000 },
];

export const MOCK_VENDOR_SPENDING = [
  { label: 'TechCorp Solutions', value: 520000 },
  { label: 'Global Logistics Inc', value: 380000 },
  { label: 'Acme Hardware Supplies', value: 290000 },
  { label: 'Apex Data Systems', value: 210000 },
  { label: 'Summit Office Solutions', value: 160000 },
];

export const MOCK_DEPARTMENT_SPENDING = [
  { label: 'IT & Infrastructure', value: 1250000 },
  { label: 'Operations & Logistics', value: 890000 },
  { label: 'Marketing & Sales', value: 540000 },
  { label: 'Human Resources', value: 320000 },
  { label: 'Administration', value: 450000 },
];

export const MOCK_INVOICE_STATUS_DISTRIBUTION = [
  { label: 'Paid', value: 285, color: '#10b981' },
  { label: 'Pending Approval', value: 28, color: '#f59e0b' },
  { label: 'Overdue', value: 7, color: '#ef4444' },
  { label: 'Rejected', value: 4, color: '#6b7280' },
];

export const MOCK_PAYMENT_STATUS_DISTRIBUTION = [
  { label: 'Completed', value: 260, color: '#10b981' },
  { label: 'Processing', value: 18, color: '#3b82f6' },
  { label: 'Scheduled', value: 12, color: '#8b5cf6' },
  { label: 'Failed', value: 3, color: '#ef4444' },
];

export const MOCK_PURCHASE_ORDERS = [
  {
    id: 'PO-2026-001',
    poNumber: 'PO-2026-001',
    vendor: 'TechCorp Solutions',
    vendorEmail: 'billing@techcorp.com',
    vendorPhone: '+1 (555) 234-5678',
    department: 'IT & Infrastructure',
    requestNumber: 'PR-2026-089',
    orderDate: '2026-07-10',
    expectedDelivery: '2026-08-01',
    totalAmount: 45000.0,
    status: 'Approved',
    deliveryStatus: 'In Transit',
    invoiceStatus: 'Invoiced',
    paymentStatus: 'Pending',
    items: [
      { name: 'Enterprise Cloud Servers', quantity: 5, price: 8000, tax: 800, discount: 0, total: 40800 },
      { name: 'High-Speed Switches', quantity: 2, price: 2100, tax: 0, discount: 0, total: 4200 }
    ],
    timeline: [
      { date: '2026-07-10 09:30', title: 'Requisition Created', by: 'John Doe (IT Lead)' },
      { date: '2026-07-11 14:15', title: 'Manager Approved', by: 'Sarah Jenkins (IT Manager)' },
      { date: '2026-07-12 11:00', title: 'Finance Approved & PO Issued', by: 'David Miller (Finance Manager)' }
    ],
    approvalHistory: [
      { step: 'Level 1: Department Manager', approver: 'Sarah Jenkins', status: 'Approved', date: '2026-07-11' },
      { step: 'Level 2: Finance Compliance', approver: 'David Miller', status: 'Approved', date: '2026-07-12' }
    ]
  },
  {
    id: 'PO-2026-002',
    poNumber: 'PO-2026-002',
    vendor: 'Global Logistics Inc',
    vendorEmail: 'accounts@globallogistics.com',
    vendorPhone: '+1 (555) 876-5432',
    department: 'Operations & Logistics',
    requestNumber: 'PR-2026-092',
    orderDate: '2026-07-15',
    expectedDelivery: '2026-07-28',
    totalAmount: 18500.0,
    status: 'Pending',
    deliveryStatus: 'Pending',
    invoiceStatus: 'Uninvoiced',
    paymentStatus: 'Unpaid',
    items: [
      { name: 'Freight Container Transport', quantity: 1, price: 18500, tax: 0, discount: 0, total: 18500 }
    ],
    timeline: [
      { date: '2026-07-15 10:00', title: 'Requisition Created', by: 'Mark Rivera' }
    ],
    approvalHistory: [
      { step: 'Level 1: Department Manager', approver: 'Mark Rivera', status: 'Approved', date: '2026-07-16' },
      { step: 'Level 2: Finance Compliance', approver: 'Pending Review', status: 'Pending', date: '-' }
    ]
  },
  {
    id: 'PO-2026-003',
    poNumber: 'PO-2026-003',
    vendor: 'Acme Hardware Supplies',
    vendorEmail: 'sales@acmehardware.com',
    vendorPhone: '+1 (555) 345-6789',
    department: 'Administration',
    requestNumber: 'PR-2026-095',
    orderDate: '2026-07-02',
    expectedDelivery: '2026-07-12',
    totalAmount: 9200.0,
    status: 'Approved',
    deliveryStatus: 'Delivered',
    invoiceStatus: 'Paid',
    paymentStatus: 'Paid',
    items: [
      { name: 'Ergonomic Office Chairs', quantity: 20, price: 460, tax: 0, discount: 0, total: 9200 }
    ],
    timeline: [
      { date: '2026-07-02 08:30', title: 'PO Created', by: 'Lisa Wong' },
      { date: '2026-07-03 16:00', title: 'PO Approved', by: 'David Miller' }
    ],
    approvalHistory: [
      { step: 'Level 1: Finance Compliance', approver: 'David Miller', status: 'Approved', date: '2026-07-03' }
    ]
  },
  {
    id: 'PO-2026-004',
    poNumber: 'PO-2026-004',
    vendor: 'Apex Data Systems',
    vendorEmail: 'support@apexdata.io',
    vendorPhone: '+1 (555) 901-2345',
    department: 'IT & Infrastructure',
    requestNumber: 'PR-2026-101',
    orderDate: '2026-07-20',
    expectedDelivery: '2026-08-10',
    totalAmount: 32000.0,
    status: 'Pending',
    deliveryStatus: 'Pending',
    invoiceStatus: 'Uninvoiced',
    paymentStatus: 'Unpaid',
    items: [
      { name: 'Database Optimization Suite', quantity: 1, price: 32000, tax: 0, discount: 0, total: 32000 }
    ],
    timeline: [
      { date: '2026-07-20 11:45', title: 'PO Created', by: 'John Doe' }
    ],
    approvalHistory: [
      { step: 'Level 1: Department Manager', approver: 'Sarah Jenkins', status: 'Approved', date: '2026-07-21' },
      { step: 'Level 2: Finance Compliance', approver: 'Pending', status: 'Pending', date: '-' }
    ]
  }
];

export const MOCK_INVOICES = [
  {
    id: 'INV-2026-101',
    invoiceNumber: 'INV-2026-101',
    vendor: 'TechCorp Solutions',
    vendorAddress: '100 Silicon Way, San Jose, CA',
    purchaseOrder: 'PO-2026-001',
    invoiceDate: '2026-07-15',
    dueDate: '2026-08-15',
    amount: 40000.0,
    tax: 5000.0,
    gst: '18% GST ($5,000)',
    discount: 0.0,
    total: 45000.0,
    status: 'Pending Approval',
    paymentStatus: 'Unpaid',
    items: [
      { description: 'Enterprise Cloud Servers', qty: 5, unitPrice: 8000, lineTotal: 40000 }
    ],
    attachments: [
      { name: 'TechCorp_Invoice_101.pdf', size: '240 KB', url: '#' }
    ]
  },
  {
    id: 'INV-2026-102',
    invoiceNumber: 'INV-2026-102',
    vendor: 'Acme Hardware Supplies',
    vendorAddress: '45 Industrial Pkwy, Chicago, IL',
    purchaseOrder: 'PO-2026-003',
    invoiceDate: '2026-07-05',
    dueDate: '2026-07-25',
    amount: 8500.0,
    tax: 700.0,
    gst: '8% GST ($700)',
    discount: 0.0,
    total: 9200.0,
    status: 'Paid',
    paymentStatus: 'Paid',
    items: [
      { description: 'Ergonomic Office Chairs', qty: 20, unitPrice: 425, lineTotal: 8500 }
    ],
    attachments: [
      { name: 'Acme_Inv_9200.pdf', size: '180 KB', url: '#' }
    ]
  },
  {
    id: 'INV-2026-103',
    invoiceNumber: 'INV-2026-103',
    vendor: 'Summit Office Solutions',
    vendorAddress: '788 Corporate Blvd, New York, NY',
    purchaseOrder: 'PO-2026-008',
    invoiceDate: '2026-07-18',
    dueDate: '2026-07-28',
    amount: 14200.0,
    tax: 1200.0,
    gst: 'GST Included',
    discount: 400.0,
    total: 15000.0,
    status: 'Overdue',
    paymentStatus: 'Unpaid',
    items: [
      { description: 'Quarterly Office Stationery & Printer Cartridges', qty: 1, unitPrice: 14200, lineTotal: 14200 }
    ],
    attachments: [
      { name: 'Summit_Inv_15000.pdf', size: '310 KB', url: '#' }
    ]
  },
  {
    id: 'INV-2026-104',
    invoiceNumber: 'INV-2026-104',
    vendor: 'Global Logistics Inc',
    vendorAddress: '500 Harbor Dr, Seattle, WA',
    purchaseOrder: 'PO-2026-002',
    invoiceDate: '2026-07-20',
    dueDate: '2026-08-20',
    amount: 18500.0,
    tax: 0.0,
    gst: 'Exempt',
    discount: 0.0,
    total: 18500.0,
    status: 'Approved',
    paymentStatus: 'Scheduled',
    items: [
      { description: 'Freight Cargo Logistics Fee', qty: 1, unitPrice: 18500, lineTotal: 18500 }
    ],
    attachments: [
      { name: 'GLI_Freight_Bill.pdf', size: '420 KB', url: '#' }
    ]
  }
];

export const MOCK_PAYMENTS = [
  {
    id: 'PAY-2026-8801',
    paymentId: 'PAY-2026-8801',
    invoiceNumber: 'INV-2026-102',
    vendor: 'Acme Hardware Supplies',
    amount: 9200.0,
    paymentDate: '2026-07-10',
    paymentMethod: 'Bank Transfer',
    referenceNumber: 'REF-TXN-9984210',
    status: 'Completed',
    remarks: 'Full settlement for Office Furniture PO-2026-003'
  },
  {
    id: 'PAY-2026-8802',
    paymentId: 'PAY-2026-8802',
    invoiceNumber: 'INV-2026-098',
    vendor: 'Apex Data Systems',
    amount: 25000.0,
    paymentDate: '2026-07-14',
    paymentMethod: 'Wire Transfer',
    referenceNumber: 'REF-WIRE-441209',
    status: 'Completed',
    remarks: 'Annual Maintenance License Renewal'
  },
  {
    id: 'PAY-2026-8803',
    paymentId: 'PAY-2026-8803',
    invoiceNumber: 'INV-2026-104',
    vendor: 'Global Logistics Inc',
    amount: 18500.0,
    paymentDate: '2026-07-28',
    paymentMethod: 'UPI',
    referenceNumber: 'UPI-9821049281',
    status: 'Scheduled',
    remarks: 'Scheduled automated payout upon delivery confirmation'
  }
];

export const MOCK_VENDOR_PAYMENT_HISTORY = [
  {
    vendor: 'TechCorp Solutions',
    totalInvoices: 12,
    totalAmount: 520000,
    paidAmount: 475000,
    pendingAmount: 45000,
    lastPaymentDate: '2026-06-28',
    status: 'Active'
  },
  {
    vendor: 'Global Logistics Inc',
    totalInvoices: 8,
    totalAmount: 380000,
    paidAmount: 361500,
    pendingAmount: 18500,
    lastPaymentDate: '2026-07-01',
    status: 'Active'
  },
  {
    vendor: 'Acme Hardware Supplies',
    totalInvoices: 15,
    totalAmount: 290000,
    paidAmount: 290000,
    pendingAmount: 0,
    lastPaymentDate: '2026-07-10',
    status: 'Clear'
  },
  {
    vendor: 'Summit Office Solutions',
    totalInvoices: 6,
    totalAmount: 160000,
    paidAmount: 145000,
    pendingAmount: 15000,
    lastPaymentDate: '2026-05-15',
    status: 'Attention Required'
  }
];

export const MOCK_AUDIT_LOGS = [
  {
    id: 'AUD-901',
    user: 'David Miller',
    role: 'Finance Manager',
    action: 'APPROVE_INVOICE',
    date: '2026-07-28',
    time: '14:22:10',
    module: 'Invoices',
    ipAddress: '192.168.1.104',
    description: 'Approved invoice INV-2026-104 for Global Logistics Inc ($18,500.00)'
  },
  {
    id: 'AUD-902',
    user: 'Sarah Jenkins',
    role: 'Finance Officer',
    action: 'CREATE_PAYMENT',
    date: '2026-07-27',
    time: '10:15:00',
    module: 'Payments',
    ipAddress: '192.168.1.112',
    description: 'Created payment entry PAY-2026-8803 scheduled for 2026-07-28'
  },
  {
    id: 'AUD-903',
    user: 'Admin System',
    role: 'System',
    action: 'GENERATE_REPORT',
    date: '2026-07-25',
    time: '18:00:00',
    module: 'Reports',
    ipAddress: '127.0.0.1',
    description: 'Automated generation of Monthly Expense Reconciliation Report for June 2026'
  },
  {
    id: 'AUD-904',
    user: 'David Miller',
    role: 'Finance Manager',
    action: 'REJECT_INVOICE',
    date: '2026-07-22',
    time: '16:45:30',
    module: 'Invoices',
    ipAddress: '192.168.1.104',
    description: 'Rejected invoice INV-2026-094 due to tax rate calculation mismatch'
  }
];

export const MOCK_NOTIFICATIONS = [
  {
    id: 'NOTIF-01',
    title: 'Invoice Approved',
    message: 'Invoice INV-2026-104 for Global Logistics Inc was approved by David Miller.',
    type: 'success',
    timestamp: '10 minutes ago',
    isRead: false
  },
  {
    id: 'NOTIF-02',
    title: 'Payment Scheduled',
    message: 'Payment of $18,500 to Global Logistics Inc is scheduled for July 28, 2026.',
    type: 'info',
    timestamp: '2 hours ago',
    isRead: false
  },
  {
    id: 'NOTIF-03',
    title: 'Overdue Payment Alert',
    message: 'Invoice INV-2026-103 for Summit Office Solutions ($15,000) is past its due date (2026-07-28).',
    type: 'warning',
    timestamp: '1 day ago',
    isRead: true
  },
  {
    id: 'NOTIF-04',
    title: 'New Vendor Registered',
    message: 'Apex Data Systems has completed onboarding and tax verification.',
    type: 'system',
    timestamp: '3 days ago',
    isRead: true
  }
];
