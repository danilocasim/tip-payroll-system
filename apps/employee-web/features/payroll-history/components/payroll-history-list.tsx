import { EmployeePayrollRecordSummary } from "@payroll/types";
import { Badge, Card, CardContent, EmptyState } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export function PayrollHistoryList({
  records,
  renderLink
}: {
  records: EmployeePayrollRecordSummary[];
  renderLink: (recordId: number) => React.ReactNode;
}) {
  if (records.length === 0) {
    return (
      <EmptyState
        title="No payroll history yet"
        description="Once payroll snapshots are saved for your account, they will appear here in a clean chronological list."
      />
    );
  }

  return (
    <div className="grid gap-4">
      {records.map((record) => (
        <Card key={record.recordId} className="bg-white/94">
          <CardContent className="flex flex-col gap-4 p-5 md:flex-row md:items-center md:justify-between">
            <div className="space-y-2">
              <Badge>{record.payPeriod ?? "No pay period"}</Badge>
              <div className="text-sm text-[var(--color-ink-500)]">Generated: {record.generatedAt ? new Date(record.generatedAt).toLocaleDateString("en-PH") : "Not available"}</div>
            </div>
            <div className="grid gap-3 md:grid-cols-4 md:items-center md:text-right">
              <div>
                <p className="text-xs uppercase tracking-[0.08em] text-[var(--color-ink-500)]">Salary</p>
                <p className="font-medium text-[var(--color-ink-900)]">{currency(record.salary)}</p>
              </div>
              <div>
                <p className="text-xs uppercase tracking-[0.08em] text-[var(--color-ink-500)]">Bonus</p>
                <p className="font-medium text-[var(--color-success)]">{currency(record.bonus)}</p>
              </div>
              <div>
                <p className="text-xs uppercase tracking-[0.08em] text-[var(--color-ink-500)]">Net Pay</p>
                <p className="font-semibold text-[var(--color-ink-900)]">{currency(record.netPay)}</p>
              </div>
              <div className="text-sm font-medium text-[var(--color-ink-900)]">{renderLink(record.recordId)}</div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
