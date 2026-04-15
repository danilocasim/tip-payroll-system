import type { ApiResponse } from "@payroll/types";

export class ApiRequestError extends Error {
  status: number;
  code: string;
  fields: Record<string, string>;

  constructor(status: number, code: string, message: string, fields: Record<string, string> = {}) {
    super(message);
    this.name = "ApiRequestError";
    this.status = status;
    this.code = code;
    this.fields = fields;
  }
}

async function parsePayload<T>(response: Response): Promise<ApiResponse<T> | null> {
  const text = await response.text();
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text) as ApiResponse<T>;
  } catch {
    return null;
  }
}

export async function apiRequest<T>(input: string, init?: RequestInit): Promise<T> {
  const response = await fetch(input, init);
  const payload = await parsePayload<T>(response);

  if (!response.ok || !payload?.ok || payload.data === undefined) {
    throw new ApiRequestError(
      response.status,
      payload?.error?.code ?? "server_error",
      payload?.error?.message ?? "request failed",
      payload?.error?.fields ?? {}
    );
  }

  return payload.data;
}
