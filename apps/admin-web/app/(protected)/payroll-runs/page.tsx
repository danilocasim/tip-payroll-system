import Link from "next/link";

import { getPayrollRuns } from "@/lib/server/backend";
import { PayrollRunsTable } from "@/features/payroll-runs/components/payroll-runs-table";
import { Button, PageHeader } from "@payroll/ui";

export default async function PayrollRunsPage() {
  const payrollRuns = await getPayrollRuns();

  return (
    <div className="space-y-8">
      <PageHeader
        title="Payroll runs"
        description="Create, review, finalize, and export payroll snapshots for a pay period with manager-only access control."
        action={
          <Button asChild>
            <Link href="/payroll-runs/new">Create payroll run</Link>
          </Button>
        }
      />

      <PayrollRunsTable payrollRuns={payrollRuns} />
    </div>
  );
}
