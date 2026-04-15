import { getPayrollRun } from "@/lib/server/backend";
import { PayrollRunDetailView } from "@/features/payroll-runs/components/payroll-run-detail-view";
import { PageHeader } from "@payroll/ui";

export default async function PayrollRunDetailPage({ params }: { params: Promise<{ payrollRunId: string }> }) {
  const { payrollRunId } = await params;
  const payrollRun = await getPayrollRun(payrollRunId);

  return (
    <div className="space-y-8">
      <PageHeader
        title={`Payroll run ${payrollRun.id}`}
        description={`Pay period: ${payrollRun.payPeriodStart} to ${payrollRun.payPeriodEnd}`}
      />
      <PayrollRunDetailView payrollRun={payrollRun} />
    </div>
  );
}
