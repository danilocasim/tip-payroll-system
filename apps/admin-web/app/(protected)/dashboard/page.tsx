import { BriefcaseBusiness, DollarSign, ReceiptText, Users } from "lucide-react";

import { MetricCard } from "@/features/dashboard/components/metric-card";
import { getAdminDashboardSummary } from "@/lib/server/backend";
import { PageHeader } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export default async function AdminDashboardPage() {
  const summary = await getAdminDashboardSummary();

  return (
    <div className="space-y-8">
      <PageHeader
        title="Operations dashboard"
        description="Monitor employee volume, payroll totals, and the current operational baseline from one clean control surface."
      />

      <section className="grid gap-5 xl:grid-cols-4 md:grid-cols-2">
        <MetricCard icon={Users} label="Total Employees" value={String(summary.totalEmployees)} accent="soft" />
        <MetricCard icon={DollarSign} label="Total Payroll" value={currency(summary.totalPayroll)} accent="strong" />
        <MetricCard icon={ReceiptText} label="Average Net Pay" value={currency(summary.avgNetPay)} accent="neutral" />
        <MetricCard icon={BriefcaseBusiness} label="Portal State" value="Protected" accent="neutral" />
      </section>
    </div>
  );
}
