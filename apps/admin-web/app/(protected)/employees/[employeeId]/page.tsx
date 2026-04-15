import Link from "next/link";
import { notFound } from "next/navigation";

import { EmployeeEditForm } from "@/features/employees/components/employee-edit-form";
import { getAdminEmployee } from "@/lib/server/backend";
import { Button, PageHeader } from "@payroll/ui";

export default async function EmployeeEditPage({ params }: { params: Promise<{ employeeId: string }> }) {
  const { employeeId: rawEmployeeId } = await params;
  const employeeId = Number(rawEmployeeId);

  if (Number.isNaN(employeeId)) {
    notFound();
  }

  const employee = await getAdminEmployee(employeeId);

  return (
    <div className="space-y-8">
      <PageHeader
        title={`Edit ${employee.name}`}
        description="Adjust payroll basis values for an existing employee before generating a payroll run."
        action={
          <Button asChild type="button" variant="secondary">
            <Link href="/employees">Back to employees</Link>
          </Button>
        }
      />

      <EmployeeEditForm employee={employee} />
    </div>
  );
}
