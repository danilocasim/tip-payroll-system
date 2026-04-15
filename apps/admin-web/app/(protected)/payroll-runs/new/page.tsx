import { PageHeader } from "@payroll/ui";

import { PayrollRunCreateForm } from "@/features/payroll-runs/components/payroll-run-create-form";

export default function NewPayrollRunPage() {
  return (
    <div className="space-y-8">
      <PageHeader
        title="Create payroll run"
        description="Generate a manager-reviewed payroll snapshot for a pay period and optional campus scope."
      />

      <PayrollRunCreateForm />
    </div>
  );
}
