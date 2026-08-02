import DashboardPageContent from "../../components/dashboard/DashboardPageContent";

const PROCUREMENT_STATS = [
  {
    icon: "bi-check-circle",
    iconVariant: "success",
    value: "18",
    label: "Approved Requests",
  },
  {
    icon: "bi-cart-check",
    iconVariant: "primary",
    value: "12",
    label: "Purchase Orders",
  },
  {
    icon: "bi-building",
    iconVariant: "warning",
    value: "25",
    label: "Vendors",
  },
  {
    icon: "bi-truck",
    iconVariant: "accent",
    value: "8",
    label: "Deliveries",
  },
];

const ProcurementOfficerDashboard = () => {
  return (
    <DashboardPageContent
      roleLabel="Procurement Officer Dashboard"
      description="Manage approved purchase requests, vendors and purchase orders."
      statCards={PROCUREMENT_STATS}
    />
  );
};

export default ProcurementOfficerDashboard;