"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { LogOut } from "lucide-react";

import { ApiRequestError } from "@payroll/api-client";
import { fetchCsrfToken } from "@payroll/auth";
import { Alert, Button } from "@payroll/ui";

const API_BASE = "/api/backend";

export function LogoutButton() {
  const router = useRouter();
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleLogout() {
    setPending(true);
    setError(null);

    try {
      const csrf = await fetchCsrfToken(API_BASE);
      const response = await fetch(`${API_BASE}/api/v1/auth/logout`, {
        method: "POST",
        credentials: "include",
        headers: {
          [csrf.headerName]: csrf.token
        }
      });

      if (!response.ok) {
        throw new ApiRequestError(response.status, "logout_failed", "Unable to sign out.");
      }

      router.push("/login");
      router.refresh();
    } catch (cause) {
      if (cause instanceof ApiRequestError) {
        setError(cause.message);
      } else {
        setError("Unable to sign out.");
      }
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="space-y-2">
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Button onClick={handleLogout} size="sm" variant="secondary" type="button">
        <LogOut className="h-4 w-4" />
        {pending ? "Signing out..." : "Sign out"}
      </Button>
    </div>
  );
}
