import type { PayrollRunDetail } from "@payroll/types";
import { Badge, Card, CardContent, CardHeader, CardTitle, Table, TableCell, TableHead, TableRow } from "@payroll/ui";

import { PayrollRunExportButton } from "./payroll-run-export-button";
import { PayrollRunFinalizeButton } from "./payroll-run-finalize-button";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export function PayrollRunDetailView({ payrollRun }: { payrollRun: PayrollRunDetail }) {
  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-3 print:hidden">
        <Badge>{payrollRun.status}</Badge>
        <PayrollRunExportButton />
        {payrollRun.status === "DRAFT" ? <PayrollRunFinalizeButton payrollRunId={payrollRun.id} /> : null}
      </div>

      <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <SummaryCard label="Employees" value={String(payrollRun.employeeCount)} />
        <SummaryCard label="Total Salary" value={currency(payrollRun.totalSalary)} />
        <SummaryCard label="Total Deductions" value={currency(payrollRun.totalDeductions)} />
        <SummaryCard label="Total Net Pay" value={currency(payrollRun.totalNetPay)} featured />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Payroll records</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-0">
          <Table>
            <thead>
              <tr>
                <TableHead scope="col">Employee</TableHead>
                <TableHead scope="col">Campus</TableHead>
                <TableHead scope="col">Position</TableHead>
                <TableHead scope="col" className="text-right">Salary</TableHead>
                <TableHead scope="col" className="text-right">Bonus</TableHead>
                <TableHead scope="col" className="text-right">Deductions</TableHead>
                <TableHead scope="col" className="text-right">Net Pay</TableHead>
              </tr>
            </thead>
            <tbody>
              {payrollRun.records.map((record) => (
                <TableRow key={record.recordId}>
                  <TableCell>
                    <div className="space-y-1">
                      <div className="font-medium text-[var(--color-ink-900)]">{record.employeeName}</div>
                      <div className="text-xs text-[var(--color-ink-500)]">Record #{record.recordId}</div>
                    </div>
                  </TableCell>
                  <TableCell>{record.campus ?? "-"}</TableCell>
                  <TableCell>{record.position ?? "-"}</TableCell>
                  <TableCell className="text-right tabular-nums">{currency(record.salary)}</TableCell>
                  <TableCell className="text-right tabular-nums">{currency(record.bonus)}</TableCell>
                  <TableCell className="text-right tabular-nums">{currency(record.deductions)}</TableCell>
                  <TableCell className="text-right font-medium tabular-nums text-[var(--color-ink-900)]">{currency(record.netPay)}</TableCell>
                </TableRow>
              ))}
            </tbody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}

function SummaryCard({ label, value, featured = false }: { label: string; value: string; featured?: boolean }) {
  return (
    <Card className={featured ? "bg-[linear-gradient(135deg,rgba(255,247,219,1),rgba(244,197,66,0.45))]" : "bg-white"}>
      <CardContent className="p-6">
        <p className="text-sm text-[var(--color-ink-500)]">{label}</p>
        <p className="mt-3 text-2xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">{value}</p>
      </CardContent>
    </Card>
  );
}
