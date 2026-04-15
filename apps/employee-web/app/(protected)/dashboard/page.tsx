import { Clock3, Landmark, Sparkles } from "lucide-react";

import { SummaryCard } from "@/features/dashboard/components/summary-card";
import { getEmployeeDashboardSummary } from "@/lib/server/backend";
import { PageHeader } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export default async function EmployeeDashboardPage() {
  const summary = await getEmployeeDashboardSummary();

  return (
    <div className="space-y-8">
      <PageHeader
        title={`Welcome, ${summary.fullName}`}
        description="Use this portal to review your latest payroll snapshot, confirm your profile details, and stay on top of recent payroll activity."
      />

      <section className="grid gap-5 md:grid-cols-3">
        <SummaryCard icon={Landmark} label="Latest Net Pay" value={currency(summary.latestNetPay)} featured />
        <SummaryCard icon={Clock3} label="Latest Pay Period" value={summary.latestPayPeriod ?? "Not available yet"} />
        <SummaryCard icon={Sparkles} label="Payroll Records" value={String(summary.payrollRecordCount)} />
      </section>
    </div>
  );
}
