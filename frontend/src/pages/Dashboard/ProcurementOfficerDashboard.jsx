import DashboardPageContent from "../../components/dashboard/DashboardPageContent";
import DashboardStatCard from "../../components/dashboard/DashboardStatCard";

const ProcurementOfficerDashboard = () => {
  const stats = [
    {
      title: "Approved Requests",
      value: "18",
      icon: "bi-check-circle",
    },
    {
      title: "Purchase Orders",
      value: "12",
      icon: "bi-cart-check",
    },
    {
      title: "Vendors",
      value: "25",
      icon: "bi-building",
    },
    {
      title: "Deliveries",
      value: "8",
      icon: "bi-truck",
    },
  ];

  return (
    <DashboardPageContent
      title="Procurement Officer Dashboard"
      subtitle="Manage procurement operations, vendors and purchase orders."
    >
      <div className="row g-4">
        {stats.map((card) => (
          <div className="col-md-6 col-xl-3" key={card.title}>
            <DashboardStatCard {...card} />
          </div>
        ))}
      </div>
    </DashboardPageContent>
  );
};

export default ProcurementOfficerDashboard;