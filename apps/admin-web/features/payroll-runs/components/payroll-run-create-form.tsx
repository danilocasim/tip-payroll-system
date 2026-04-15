"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { ClipboardPlus } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import { fetchCsrfToken } from "@payroll/auth";
import { CAMPUS_OPTIONS, type CreatePayrollRunRequest, type PayrollRunDetail } from "@payroll/types";
import { Alert, Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input, Select } from "@payroll/ui";

const API_BASE = "/api/backend";

const initialValues: CreatePayrollRunRequest = {
  payPeriodStart: "",
  payPeriodEnd: "",
  campusScope: ""
};

export function PayrollRunCreateForm() {
  const router = useRouter();
  const [values, setValues] = useState<CreatePayrollRunRequest>(initialValues);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isDisabled = useMemo(() => submitting || !values.payPeriodStart || !values.payPeriodEnd, [submitting, values]);

  function update(field: keyof CreatePayrollRunRequest) {
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
      const payrollRun = await apiRequest<PayrollRunDetail>(`${API_BASE}/api/v1/admin/payroll-runs`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token
        },
        body: JSON.stringify({
          ...values,
          campusScope: values.campusScope?.trim() ? values.campusScope.trim() : undefined
        })
      });

      router.push(`/payroll-runs/${payrollRun.id}`);
      router.refresh();
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to create payroll run.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card className="max-w-3xl">
      <CardHeader>
        <CardTitle>Create payroll run</CardTitle>
        <CardDescription>
          This captures a payroll snapshot for the selected pay period. Leave campus scope blank to include all campuses.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-5" onSubmit={handleSubmit}>
          {error ? <Alert tone="error">{error}</Alert> : null}

          <div className="grid gap-4 md:grid-cols-2">
            <Field label="Pay Period Start">
              <Input type="date" value={values.payPeriodStart} onChange={update("payPeriodStart")} />
            </Field>
            <Field label="Pay Period End">
              <Input type="date" value={values.payPeriodEnd} onChange={update("payPeriodEnd")} />
            </Field>
            <Field label="Campus Scope">
              <Select value={values.campusScope ?? ""} onChange={update("campusScope")}>
                <option value="">All campuses</option>
                {CAMPUS_OPTIONS.map((campus) => (
                  <option key={campus} value={campus}>
                    {campus}
                  </option>
                ))}
              </Select>
            </Field>
          </div>

          <div className="flex flex-wrap gap-3">
            <Button disabled={isDisabled} type="submit">
              <ClipboardPlus className="h-4 w-4" />
              {submitting ? "Creating..." : "Create payroll run"}
            </Button>
            <Button asChild type="button" variant="secondary">
              <Link href="/payroll-runs">Back to payroll runs</Link>
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="space-y-2 text-sm font-medium text-[var(--color-ink-700)]">
      <span>{label}</span>
      {children}
    </label>
  );
}
