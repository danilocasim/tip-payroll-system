import "server-only";

import { cookies } from "next/headers";
import { redirect } from "next/navigation";

import type { AdminDashboardSummary, AdminEmployeeDetail, AdminEmployeesPage, AuthMeData, AuthUser, PayrollRunDetail, PayrollRunListItem } from "@payroll/types";

const API_BASE = process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

class BackendRequestError extends Error {
  status: number;
  code: string;

  constructor(status: number, code: string, message: string) {
    super(message);
    this.name = "BackendRequestError";
    this.status = status;
    this.code = code;
  }
}

async function backendRequest<T>(path: string): Promise<T> {
  const cookieHeader = (await cookies()).toString();
  const headers = new Headers();
  if (cookieHeader) {
    headers.set("Cookie", cookieHeader);
  }

  const response = await fetch(`${API_BASE}${path}`, {
    cache: "no-store",
    headers
  });

  const text = await response.text();
  let payload: { ok?: boolean; data?: T; error?: { code?: string; message?: string } } | null = null;
  if (text) {
    try {
      payload = JSON.parse(text) as { ok?: boolean; data?: T; error?: { code?: string; message?: string } };
    } catch {
      payload = null;
    }
  }

  if (!response.ok || !payload?.ok || payload.data === undefined) {
    throw new BackendRequestError(response.status, payload?.error?.code ?? "server_error", payload?.error?.message ?? "Request failed");
  }

  return payload.data;
}

function handleProtectedError(error: unknown): never {
  if (error instanceof BackendRequestError && error.status === 401) {
    redirect("/login");
  }
  if (error instanceof BackendRequestError && error.status === 403) {
    redirect("/access-denied");
  }
  throw error;
}

export async function requireAdminUser(): Promise<AuthUser> {
  try {
    const session = await backendRequest<AuthMeData>("/api/v1/auth/me");
    if (session.user.role !== "MANAGER") {
      redirect("/access-denied");
    }
    return session.user;
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getAdminDashboardSummary(): Promise<AdminDashboardSummary> {
  try {
    return await backendRequest<AdminDashboardSummary>("/api/v1/admin/dashboard/summary");
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getAdminEmployees({ search, campus }: { search?: string; campus?: string } = {}): Promise<AdminEmployeesPage> {
  try {
    const query = new URLSearchParams({ page: "1", perPage: "25" });
    if (search?.trim()) {
      query.set("search", search.trim());
    }
    if (campus?.trim()) {
      query.set("campus", campus.trim());
    }

    return await backendRequest<AdminEmployeesPage>(`/api/v1/admin/employees?${query.toString()}`);
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getAdminEmployee(employeeId: number): Promise<AdminEmployeeDetail> {
  try {
    return await backendRequest<AdminEmployeeDetail>(`/api/v1/admin/employees/${employeeId}`);
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getPayrollRuns(): Promise<PayrollRunListItem[]> {
  try {
    return await backendRequest<PayrollRunListItem[]>("/api/v1/admin/payroll-runs");
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getPayrollRun(payrollRunId: string): Promise<PayrollRunDetail> {
  try {
    return await backendRequest<PayrollRunDetail>(`/api/v1/admin/payroll-runs/${payrollRunId}`);
  } catch (error) {
    handleProtectedError(error);
  }
}
