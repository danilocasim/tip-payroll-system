import Link from "next/link";

import { AdminEmployeeListItem } from "@payroll/types";
import { Badge, Button, Card, CardContent, EmptyState, Table, TableCell, TableHead, TableRow } from "@payroll/ui";

function currency(value: string): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(value));
}

export function EmployeesTable({ employees }: { employees: AdminEmployeeListItem[] }) {
  if (employees.length === 0) {
    return (
      <EmptyState
        title="No employees yet"
        description="Employee management is already protected and ready. Add records once the create/edit flow is connected in the next slice."
      />
    );
  }

  return (
    <Card>
      <CardContent className="overflow-x-auto p-0">
        <Table>
          <thead>
            <tr>
              <TableHead scope="col">Name</TableHead>
              <TableHead scope="col">Campus</TableHead>
              <TableHead scope="col">Position</TableHead>
              <TableHead scope="col">Payroll Basis</TableHead>
              <TableHead scope="col">Pay Period</TableHead>
              <TableHead scope="col" className="text-right">Net Pay</TableHead>
              <TableHead scope="col" className="text-right">Actions</TableHead>
            </tr>
          </thead>
          <tbody>
            {employees.map((employee) => (
              <TableRow key={employee.employeeId}>
                <TableCell>
                  <div className="space-y-1">
                    <div className="font-medium text-[var(--color-ink-900)]">{employee.name}</div>
                    {employee.workArea ? <div className="text-xs text-[var(--color-ink-500)]">{employee.workArea}</div> : null}
                  </div>
                </TableCell>
                <TableCell>
                  {employee.campus ? <Badge>{employee.campus}</Badge> : <span className="text-[var(--color-ink-500)]">Unassigned</span>}
                </TableCell>
                <TableCell>{employee.position ?? "-"}</TableCell>
                <TableCell>
                  <div className="space-y-1 text-xs text-[var(--color-ink-500)]">
                    <div>Rate: {currency(employee.hourlyRate)}</div>
                    <div>Hours: {Number(employee.hoursWorked).toFixed(2)}</div>
                  </div>
                </TableCell>
                <TableCell>{employee.payPeriod ?? "-"}</TableCell>
                <TableCell className="text-right font-medium tabular-nums text-[var(--color-ink-900)]">{currency(employee.netPay)}</TableCell>
                <TableCell className="text-right">
                  <Button asChild size="sm" type="button" variant="secondary">
                    <Link href={`/employees/${employee.employeeId}`}>Edit</Link>
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
