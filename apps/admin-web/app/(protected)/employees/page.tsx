import Link from "next/link";

import { EmployeesFilters } from "@/features/employees/components/employees-filters";
import { getAdminEmployees } from "@/lib/server/backend";
import { EmployeesTable } from "@/features/employees/components/employees-table";
import { Button, PageHeader } from "@payroll/ui";

export default async function EmployeesPage({
  searchParams
}: {
  searchParams: Promise<{ search?: string; campus?: string }>;
}) {
  const params = await searchParams;
  const employees = await getAdminEmployees({
    ...(params.search ? { search: params.search } : {}),
    ...(params.campus ? { campus: params.campus } : {})
  });

  return (
    <div className="space-y-8">
      <PageHeader
        title="Employees"
        description="Review employee records, payroll values, and work assignments from a single manager-facing table."
        action={
          <Button asChild>
            <Link href="/employees/new">Add employee</Link>
          </Button>
        }
      />

      <EmployeesFilters currentCampus={params.campus ?? ""} currentSearch={params.search ?? ""} />

      <EmployeesTable employees={employees.items} />
    </div>
  );
}
