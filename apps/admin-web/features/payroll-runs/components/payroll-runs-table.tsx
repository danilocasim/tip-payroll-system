import Link from "next/link";

import type { PayrollRunListItem } from "@payroll/types";
import { Badge, Button, Card, CardContent, EmptyState, Table, TableCell, TableHead, TableRow } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export function PayrollRunsTable({ payrollRuns }: { payrollRuns: PayrollRunListItem[] }) {
  if (payrollRuns.length === 0) {
    return (
      <EmptyState
        title="No payroll runs yet"
        description="Create the first payroll run to capture a reviewable payroll snapshot for the selected pay period."
      />
    );
  }

  return (
    <Card>
      <CardContent className="overflow-x-auto p-0">
        <Table>
          <thead>
            <tr>
              <TableHead scope="col">Pay Period</TableHead>
              <TableHead scope="col">Campus</TableHead>
              <TableHead scope="col">Status</TableHead>
              <TableHead scope="col">Employees</TableHead>
              <TableHead scope="col" className="text-right">Net Pay</TableHead>
              <TableHead scope="col" className="text-right">Actions</TableHead>
            </tr>
          </thead>
          <tbody>
            {payrollRuns.map((payrollRun) => (
              <TableRow key={payrollRun.id}>
                <TableCell>
                  <div className="space-y-1">
                    <div className="font-medium text-[var(--color-ink-900)]">{payrollRun.payPeriodStart} to {payrollRun.payPeriodEnd}</div>
                    <div className="text-xs text-[var(--color-ink-500)]">Created: {new Date(payrollRun.createdAt).toLocaleString("en-PH")}</div>
                  </div>
                </TableCell>
                <TableCell>{payrollRun.campusScope ?? "All campuses"}</TableCell>
                <TableCell><Badge>{payrollRun.status}</Badge></TableCell>
                <TableCell>{payrollRun.employeeCount}</TableCell>
                <TableCell className="text-right font-medium tabular-nums text-[var(--color-ink-900)]">{currency(payrollRun.totalNetPay)}</TableCell>
                <TableCell className="text-right">
                  <Button asChild size="sm" variant="secondary">
                    <Link href={`/payroll-runs/${payrollRun.id}`}>Review</Link>
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </tbody>
        </Table>
      </CardContent>
    </Card>
  );
}
