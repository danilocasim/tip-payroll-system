"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { CheckCircle2 } from "lucide-react";

import { ApiRequestError, apiRequest } from "@payroll/api-client";
import { fetchCsrfToken } from "@payroll/auth";
import type { PayrollRunDetail } from "@payroll/types";
import { Alert, Button } from "@payroll/ui";

const API_BASE = "/api/backend";

export function PayrollRunFinalizeButton({ payrollRunId }: { payrollRunId: string }) {
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleFinalize() {
    setPending(true);
    setError(null);

    try {
      const csrf = await fetchCsrfToken(API_BASE);
      await apiRequest<PayrollRunDetail>(`${API_BASE}/api/v1/admin/payroll-runs/${payrollRunId}/finalize`, {
        method: "POST",
        credentials: "include",
        headers: {
          [csrf.headerName]: csrf.token
        }
      });

      router.refresh();
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to finalize payroll run.");
      }
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="space-y-3">
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Button onClick={handleFinalize} type="button">
        <CheckCircle2 className="h-4 w-4" />
        {pending ? "Finalizing..." : "Finalize payroll run"}
      </Button>
    </div>
  );
}
