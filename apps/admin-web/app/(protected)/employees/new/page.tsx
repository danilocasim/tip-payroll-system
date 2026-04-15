import { PageHeader } from "@payroll/ui";

import { EmployeeProvisionForm } from "@/features/employees/components/employee-provision-form";

export default function NewEmployeePage() {
  return (
    <div className="space-y-8">
      <PageHeader
        title="Create employee"
        description="Create the payroll employee record and optionally provision employee portal access with a one-time invite link."
      />

      <EmployeeProvisionForm />
    </div>
  );
}
