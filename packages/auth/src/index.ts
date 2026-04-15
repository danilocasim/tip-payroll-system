import type { PortalRole } from "@payroll/types";

export function getCookieValue(cookieString: string, name: string): string | null {
  const entry = cookieString
    .split(";")
    .map((part) => part.trim())
    .find((part) => part.startsWith(`${name}=`));

  return entry ? decodeURIComponent(entry.slice(name.length + 1)) : null;
}

export function getBrowserCsrfToken(): string | null {
  if (typeof document === "undefined") {
    return null;
  }

  return getCookieValue(document.cookie, "XSRF-TOKEN");
}

export function roleMatches(role: string, expected: PortalRole): boolean {
  return role === expected;
}

export async function fetchCsrfToken(apiBase: string): Promise<{ token: string; headerName: string }> {
  void apiBase;

  const cookieToken = getBrowserCsrfToken();
  if (cookieToken) {
    return {
      token: cookieToken,
      headerName: "X-XSRF-TOKEN"
    };
  }

  throw new Error("Unable to fetch CSRF token");
}
