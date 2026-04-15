"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { CheckCircle2, KeyRound } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import type { EmployeeInviteView } from "@payroll/types";
import { Alert, Button, Card, CardContent, CardDescription, CardHeader, CardTitle, Input } from "@payroll/ui";

const API_BASE = "/api/backend";

export function EmployeeInviteActivationForm({ token }: { token: string }) {
  const router = useRouter();
  const [invite, setInvite] = useState<EmployeeInviteView | null>(null);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const isDisabled = useMemo(() => loading || submitting || !password.trim() || !confirmPassword.trim(), [loading, submitting, password, confirmPassword]);

  useEffect(() => {
    let mounted = true;

    apiRequest<EmployeeInviteView>(`${API_BASE}/api/v1/employee/auth/invite?token=${encodeURIComponent(token)}`, {
      credentials: "include"
    })
      .then((payload) => {
        if (mounted) {
          setInvite(payload);
          setError(null);
        }
      })
      .catch((cause) => {
        if (mounted) {
          if (cause instanceof ApiRequestError) {
            setError(cause.message);
          } else {
            setError("Unable to validate this invite link.");
          }
        }
      })
      .finally(() => {
        if (mounted) {
          setLoading(false);
        }
      });

    return () => {
      mounted = false;
    };
  }, [token]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await apiRequest<{ activated: boolean }>(`${API_BASE}/api/v1/employee/auth/activate`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ token, password, confirmPassword })
      });

      setSuccess(true);
      setTimeout(() => {
        router.push("/login");
        router.refresh();
      }, 1200);
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to activate your employee account.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card className="bg-white/94 backdrop-blur-sm">
      <CardHeader>
        <CardTitle>Set your password</CardTitle>
        <CardDescription>
          {invite ? `Invite for ${invite.employeeName} (${invite.email})` : "Validating your invite link..."}
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {error ? <Alert tone="error">{error}</Alert> : null}
        {success ? (
          <Alert tone="success">
            <div className="flex items-center gap-2 font-medium">
              <CheckCircle2 className="h-4 w-4" />
              Account activated. Redirecting to employee login...
            </div>
          </Alert>
        ) : null}

        {loading ? (
          <p className="text-sm text-[var(--color-ink-500)]">Checking invite link...</p>
        ) : invite ? (
          <form className="space-y-4" onSubmit={handleSubmit}>
            <Field label="Password">
              <Input type="password" value={password} onChange={(event: ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)} />
            </Field>
            <Field label="Confirm Password">
              <Input type="password" value={confirmPassword} onChange={(event: ChangeEvent<HTMLInputElement>) => setConfirmPassword(event.target.value)} />
            </Field>
            <p className="text-xs text-[var(--color-ink-500)]">This invite expires on {new Date(invite.expiresAt).toLocaleString("en-PH")}.</p>
            <div className="flex flex-wrap gap-3">
              <Button disabled={isDisabled || success} type="submit">
                <KeyRound className="h-4 w-4" />
                {submitting ? "Activating..." : "Activate employee access"}
              </Button>
              <Button asChild type="button" variant="secondary">
                <Link href="/login">Back to login</Link>
              </Button>
            </div>
          </form>
        ) : null}
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
