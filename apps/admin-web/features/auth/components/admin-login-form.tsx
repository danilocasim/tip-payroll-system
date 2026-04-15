"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { LockKeyhole, ShieldCheck } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import type { AuthUser } from "@payroll/types";
import { Alert, Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input } from "@payroll/ui";

const API_BASE = "/api/backend";

export function AdminLoginForm() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const isDisabled = useMemo(() => !email.trim() || !password.trim() || submitting, [email, password, submitting]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await apiRequest<AuthUser>(`${API_BASE}/api/v1/admin/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
      });

      router.push("/dashboard");
      router.refresh();
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to sign in right now.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card className="relative overflow-hidden bg-white/92 backdrop-blur-sm">
      <div className="absolute inset-x-0 top-0 h-1 bg-gradient-to-r from-[var(--color-primary-soft)] via-[var(--color-primary)] to-[var(--color-primary-strong)]" />
      <CardHeader className="space-y-5">
        <div className="flex h-14 w-14 items-center justify-center rounded-[18px] bg-[var(--color-surface-tint)] text-[var(--color-ink-900)] shadow-[var(--shadow-sm)]">
          <ShieldCheck className="h-7 w-7" />
        </div>
        <div className="space-y-2">
          <CardTitle>Manager Portal</CardTitle>
          <CardDescription>
            Sign in with a manager account to access protected payroll operations, employee management, and reporting.
          </CardDescription>
        </div>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit}>
          {error ? <Alert tone="error">{error}</Alert> : null}
          <div className="space-y-2">
            <label className="text-sm font-medium text-[var(--color-ink-700)]" htmlFor="admin-email">
              Email
            </label>
            <Input id="admin-email" type="email" autoComplete="email" value={email} onChange={(event: ChangeEvent<HTMLInputElement>) => setEmail(event.target.value)} />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-medium text-[var(--color-ink-700)]" htmlFor="admin-password">
              Password
            </label>
            <Input id="admin-password" type="password" autoComplete="current-password" value={password} onChange={(event: ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)} />
          </div>
          <Button className="w-full" disabled={isDisabled} type="submit">
            <LockKeyhole className="h-4 w-4" />
            {submitting ? "Signing in..." : "Enter manager portal"}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
