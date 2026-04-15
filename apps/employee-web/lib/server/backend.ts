import "server-only";

import { cookies } from "next/headers";
import { notFound, redirect } from "next/navigation";

import type {
  AuthMeData,
  AuthUser,
  EmployeeDashboardSummary,
  EmployeePayrollRecordDetail,
  EmployeePayrollRecordsPage,
  EmployeeProfile
} from "@payroll/types";

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

export async function requireEmployeeUser(): Promise<AuthUser> {
  try {
    const session = await backendRequest<AuthMeData>("/api/v1/auth/me");
    if (session.user.role !== "EMPLOYEE") {
      redirect("/access-denied");
    }
    return session.user;
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getEmployeeDashboardSummary(): Promise<EmployeeDashboardSummary> {
  try {
    return await backendRequest<EmployeeDashboardSummary>("/api/v1/employee/dashboard/summary");
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getEmployeeProfile(): Promise<EmployeeProfile> {
  try {
    return await backendRequest<EmployeeProfile>("/api/v1/employee/profile");
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getEmployeePayrollRecords(): Promise<EmployeePayrollRecordsPage> {
  try {
    return await backendRequest<EmployeePayrollRecordsPage>("/api/v1/employee/payroll-records?page=1&perPage=10");
  } catch (error) {
    handleProtectedError(error);
  }
}

export async function getEmployeePayrollRecord(recordId: number): Promise<EmployeePayrollRecordDetail> {
  try {
    return await backendRequest<EmployeePayrollRecordDetail>(`/api/v1/employee/payroll-records/${recordId}`);
  } catch (error) {
    if (error instanceof BackendRequestError && error.status === 404) {
      notFound();
    }
    handleProtectedError(error);
  }
}
