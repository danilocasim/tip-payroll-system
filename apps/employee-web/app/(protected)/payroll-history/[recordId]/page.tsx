import { notFound } from "next/navigation";

import { getEmployeePayrollRecord } from "@/lib/server/backend";
import { ExportPdfButton } from "@/features/payroll-history/components/export-pdf-button";
import { Card, CardContent, CardHeader, CardTitle, PageHeader } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export default async function PayrollRecordDetailPage({ params }: { params: Promise<{ recordId: string }> }) {
  const { recordId: rawRecordId } = await params;
  const recordId = Number(rawRecordId);
  if (Number.isNaN(recordId)) {
    notFound();
  }

  const record = await getEmployeePayrollRecord(recordId);

  return (
    <div className="space-y-8">
      <PageHeader
        title={`Payroll record ${record.recordId}`}
        description={`Pay period: ${record.payPeriod ?? "Not available"}`}
        action={<ExportPdfButton />}
      />

      <Card>
        <CardHeader>
          <CardTitle>{record.employeeName}</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2">
          <div className="rounded-[18px] border border-[var(--color-line)] bg-[var(--color-surface-tint)]/70 p-4">
            <p className="text-sm text-[var(--color-ink-500)]">Gross Pay</p>
            <p className="mt-2 text-2xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">{currency(record.salary)}</p>
          </div>
          <div className="rounded-[18px] border border-[var(--color-line)] bg-white p-4">
            <p className="text-sm text-[var(--color-ink-500)]">Net Pay</p>
            <p className="mt-2 text-2xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">{currency(record.netPay)}</p>
          </div>
          <div className="rounded-[18px] border border-[var(--color-line)] bg-white p-4">
            <p className="text-sm text-[var(--color-ink-500)]">Bonus</p>
            <p className="mt-2 text-xl font-semibold text-[var(--color-success)]">{currency(record.bonus)}</p>
          </div>
          <div className="rounded-[18px] border border-[var(--color-line)] bg-white p-4">
            <p className="text-sm text-[var(--color-ink-500)]">Deductions</p>
            <p className="mt-2 text-xl font-semibold text-[var(--color-error)]">{currency(record.deductions)}</p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
