"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { Save } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import { fetchCsrfToken } from "@payroll/auth";
import { CAMPUS_OPTIONS, type AdminEmployeeDetail, type AdminUpdateEmployeeRequest } from "@payroll/types";
import { Alert, Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input, Select } from "@payroll/ui";

import { PayrollPreviewCard } from "./payroll-preview-card";

const API_BASE = "/api/backend";

export function EmployeeEditForm({ employee }: { employee: AdminEmployeeDetail }) {
  const router = useRouter();
  const [values, setValues] = useState<AdminUpdateEmployeeRequest>({
    firstName: employee.firstName,
    lastName: employee.lastName,
    campus: employee.campus,
    position: employee.position ?? "",
    workArea: employee.workArea ?? "",
    hourlyRate: employee.hourlyRate,
    hoursWorked: employee.hoursWorked,
    bonus: employee.bonus,
    deductions: employee.deductions,
    payPeriod: normalizePayPeriodValue(employee.payPeriod)
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const payrollPreview = useMemo(() => ({
    hourlyRate: Number(values.hourlyRate || "0") || 0,
    hoursWorked: Number(values.hoursWorked || "0") || 0,
    bonus: Number(values.bonus || "0") || 0,
    deductions: Number(values.deductions || "0") || 0
  }), [values]);

  function update(field: keyof AdminUpdateEmployeeRequest) {
    return (event: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      setValues((current) => ({ ...current, [field]: event.target.value }));
    };
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const csrf = await fetchCsrfToken(API_BASE);
      await apiRequest<AdminEmployeeDetail>(`${API_BASE}/api/v1/admin/employees/${employee.employeeId}`, {
        method: "PATCH",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token
        },
        body: JSON.stringify(values)
      });

      router.push("/employees");
      router.refresh();
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to update employee.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_340px]">
      <Card>
        <CardHeader>
          <CardTitle>Edit payroll basis</CardTitle>
          <CardDescription>
            Update the values that drive payroll generation for this employee. This is the fastest way to correct bad legacy payroll data.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-6" onSubmit={handleSubmit}>
            {error ? <Alert tone="error">{error}</Alert> : null}

            <section className="grid gap-4 md:grid-cols-2">
              <Field label="First Name">
                <Input value={values.firstName} onChange={update("firstName")} />
              </Field>
              <Field label="Last Name">
                <Input value={values.lastName} onChange={update("lastName")} />
              </Field>
              <Field label="Campus">
                <Select value={values.campus} onChange={update("campus")}>
                  <option value="">Select campus</option>
                  {CAMPUS_OPTIONS.map((campus) => (
                    <option key={campus} value={campus}>
                      {campus}
                    </option>
                  ))}
                </Select>
              </Field>
              <Field label="Pay Period">
                <Input type="date" value={values.payPeriod} onChange={update("payPeriod")} />
              </Field>
              <Field label="Position">
                <Input value={values.position} onChange={update("position")} />
              </Field>
              <Field label="Work Area">
                <Input value={values.workArea} onChange={update("workArea")} />
              </Field>
              <Field label="Hourly Rate">
                <Input type="number" step="0.01" value={values.hourlyRate} onChange={update("hourlyRate")} />
              </Field>
              <Field label="Hours Worked">
                <Input type="number" step="0.01" value={values.hoursWorked} onChange={update("hoursWorked")} />
              </Field>
              <Field label="Bonus">
                <Input type="number" step="0.01" value={values.bonus} onChange={update("bonus")} />
              </Field>
              <Field label="Deductions">
                <Input type="number" step="0.01" value={values.deductions} onChange={update("deductions")} />
              </Field>
            </section>

            <PayrollPreviewCard
              bonus={payrollPreview.bonus}
              deductions={payrollPreview.deductions}
              hourlyRate={payrollPreview.hourlyRate}
              hoursWorked={payrollPreview.hoursWorked}
            />

            <Button disabled={submitting} type="submit">
              <Save className="h-4 w-4" />
              {submitting ? "Saving..." : "Save employee"}
            </Button>
          </form>
        </CardContent>
      </Card>

      <Card className="h-fit">
        <CardHeader>
          <CardTitle>Current values</CardTitle>
          <CardDescription>Use these values to review and clean up legacy payroll data already stored for this employee.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-3 text-sm text-[var(--color-ink-700)]">
          <div>Employee ID: {employee.employeeId}</div>
          <div>Current Gross Pay: {new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(employee.salary))}</div>
          <div>Current Net Pay: {new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(employee.netPay))}</div>
        </CardContent>
      </Card>
    </div>
  );
}

function normalizePayPeriodValue(value: string | null): string {
  if (!value) {
    return "";
  }
  return /^\d{4}-\d{2}-\d{2}$/.test(value) ? value : "";
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="space-y-2 text-sm font-medium text-[var(--color-ink-700)]">
      <span>{label}</span>
      {children}
    </label>
  );
}
