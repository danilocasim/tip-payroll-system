import Link from "next/link";

import { getEmployeePayrollRecords } from "@/lib/server/backend";
import { PayrollHistoryList } from "@/features/payroll-history/components/payroll-history-list";
import { PageHeader } from "@payroll/ui";

export default async function PayrollHistoryPage() {
  const payrollRecords = await getEmployeePayrollRecords();

  return (
    <div className="space-y-8">
      <PageHeader
        title="Payroll history"
        description="Review your payroll records by period. Open a record to see the full breakdown of salary, bonus, deductions, and net pay."
      />

      <PayrollHistoryList
        records={payrollRecords.items}
        renderLink={(recordId) => <Link href={`/payroll-history/${recordId}`}>Open record</Link>}
      />
    </div>
  );
}
