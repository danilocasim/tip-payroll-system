"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useMemo, useState } from "react";
import Link from "next/link";
import { CheckCircle2, CopyPlus, Send } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import { fetchCsrfToken } from "@payroll/auth";
import { CAMPUS_OPTIONS, type AdminCreateEmployeeRequest, type ProvisionedEmployee } from "@payroll/types";
import { Alert, Badge, Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input, Select } from "@payroll/ui";

import { PayrollPreviewCard } from "./payroll-preview-card";

const API_BASE = "/api/backend";

const initialValues: AdminCreateEmployeeRequest = {
  employeeNumber: "",
  firstName: "",
  lastName: "",
  campus: "",
  position: "",
  workArea: "",
  hourlyRate: "0",
  hoursWorked: "0",
  bonus: "0",
  deductions: "0",
  payPeriod: "",
  createPortalAccess: true,
  email: ""
};

export function EmployeeProvisionForm() {
  const [values, setValues] = useState<AdminCreateEmployeeRequest>(initialValues);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<ProvisionedEmployee | null>(null);

  const isDisabled = useMemo(() => {
    if (submitting) {
      return true;
    }
    if (!values.employeeNumber.trim() || !values.firstName.trim() || !values.lastName.trim()) {
      return true;
    }
    if (values.createPortalAccess && !values.email?.trim()) {
      return true;
    }
    return false;
  }, [submitting, values]);

  const payrollPreview = useMemo(() => {
    const hourlyRate = Number(values.hourlyRate || "0");
    const hoursWorked = Number(values.hoursWorked || "0");
    const bonus = Number(values.bonus || "0");
    const deductions = Number(values.deductions || "0");

    return {
      hourlyRate: Number.isFinite(hourlyRate) ? hourlyRate : 0,
      hoursWorked: Number.isFinite(hoursWorked) ? hoursWorked : 0,
      bonus: Number.isFinite(bonus) ? bonus : 0,
      deductions: Number.isFinite(deductions) ? deductions : 0
    };
  }, [values]);

  function update(field: keyof AdminCreateEmployeeRequest) {
    return (event: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      const nextValue = field === "createPortalAccess" && "checked" in event.target
        ? event.target.checked
        : event.target.value;
      setValues((current) => ({ ...current, [field]: nextValue }));
    };
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setResult(null);

    try {
      const csrf = await fetchCsrfToken(API_BASE);
      const payload = await apiRequest<ProvisionedEmployee>(`${API_BASE}/api/v1/admin/employees`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token
        },
        body: JSON.stringify({
          ...values,
          email: values.createPortalAccess ? values.email?.trim() : undefined
        })
      });

      setResult(payload);
      setValues(initialValues);
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to create employee right now.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  async function copyInviteUrl() {
    if (!result?.inviteUrl) {
      return;
    }
    await navigator.clipboard.writeText(result.inviteUrl);
  }

  return (
    <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_360px]">
      <Card>
        <CardHeader>
          <CardTitle>Employee provisioning</CardTitle>
          <CardDescription>
            Use this flow for real-world onboarding: create the employee record, optionally create portal access, and send the invite link privately.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-6" onSubmit={handleSubmit}>
            {error ? <Alert tone="error">{error}</Alert> : null}

            <section className="grid gap-4 md:grid-cols-2">
              <Field label="Employee Number">
                <Input value={values.employeeNumber} onChange={update("employeeNumber")} />
              </Field>
              <Field label="Pay Period">
                <Input type="date" value={values.payPeriod} onChange={update("payPeriod")} />
              </Field>
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

            <section className="space-y-4 rounded-[20px] border border-[var(--color-line)] bg-[var(--color-surface-tint)]/65 p-5">
              <div className="flex items-start justify-between gap-4">
                <div className="space-y-1">
                  <h3 className="text-base font-semibold text-[var(--color-ink-900)]">Employee portal access</h3>
                  <p className="text-sm text-[var(--color-ink-500)]">
                    Keep public signup disabled. Provision access only when HR wants this employee to use the employee portal.
                  </p>
                </div>
                <label className="inline-flex items-center gap-2 text-sm font-medium text-[var(--color-ink-700)]">
                  <input checked={values.createPortalAccess} className="h-4 w-4 accent-[var(--color-primary-strong)]" type="checkbox" onChange={update("createPortalAccess")} />
                  Enable portal access
                </label>
              </div>

              <Field label="Employee Portal Email">
                <Input disabled={!values.createPortalAccess} type="email" value={values.email ?? ""} onChange={update("email")} />
              </Field>
            </section>

            <div className="flex flex-wrap gap-3">
              <Button disabled={isDisabled} type="submit">
                <Send className="h-4 w-4" />
                {submitting ? "Provisioning..." : "Create employee"}
              </Button>
              <Button asChild type="button" variant="secondary">
                <Link href="/employees">Back to employees</Link>
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <Card className="h-fit">
        <CardHeader>
          <CardTitle>Provisioning result</CardTitle>
          <CardDescription>
            After a successful create, share the invite link securely with the employee so they can set their first password.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {result ? (
            <>
              <Alert tone="success">
                <div className="flex items-center gap-2 font-medium">
                  <CheckCircle2 className="h-4 w-4" />
                  Employee created successfully
                </div>
              </Alert>
              <div className="space-y-2 rounded-[18px] border border-[var(--color-line)] bg-white p-4 text-sm text-[var(--color-ink-700)]">
                <div className="font-medium text-[var(--color-ink-900)]">{result.fullName}</div>
                <div>Employee Number: {result.employeeNumber}</div>
                <div>Portal Access: {result.portalAccessCreated ? <Badge>Provisioned</Badge> : <Badge>No portal</Badge>}</div>
                {result.email ? <div>Email: {result.email}</div> : null}
              </div>

              {result.inviteUrl ? (
                <div className="space-y-3 rounded-[18px] border border-[var(--color-line)] bg-[var(--color-surface-tint)] p-4">
                  <p className="text-sm font-medium text-[var(--color-ink-900)]">Invite link</p>
                  <p className="break-all text-sm text-[var(--color-ink-700)]">{result.inviteUrl}</p>
                  <p className="text-xs text-[var(--color-ink-500)]">Expires: {result.inviteExpiresAt ? new Date(result.inviteExpiresAt).toLocaleString("en-PH") : "-"}</p>
                  <Button onClick={copyInviteUrl} type="button" variant="secondary">
                    <CopyPlus className="h-4 w-4" />
                    Copy invite link
                  </Button>
                </div>
              ) : (
                <p className="text-sm text-[var(--color-ink-500)]">Portal access was not provisioned for this employee.</p>
              )}
            </>
          ) : (
            <p className="text-sm leading-6 text-[var(--color-ink-500)]">
              No provisioning result yet. Once the employee is created, their invite link and account details will appear here.
            </p>
          )}
        </CardContent>
      </Card>
    </div>
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
