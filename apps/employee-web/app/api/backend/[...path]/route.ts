import { NextRequest } from "next/server";

const API_BASE = process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

function isAllowedPath(path: string[]): boolean {
  const joined = `/${path.join("/")}`;
  return (
    joined === "/api/v1/employee/auth/login" ||
    joined === "/api/v1/auth/csrf" ||
    joined === "/api/v1/auth/me" ||
    joined === "/api/v1/auth/logout" ||
    joined.startsWith("/api/v1/employee/")
  );
}

async function proxy(request: NextRequest, context: { params: Promise<{ path: string[] }> }) {
  const { path } = await context.params;
  if (!isAllowedPath(path)) {
    return Response.json(
      { ok: false, error: { code: "forbidden", message: "proxy path not allowed" } },
      { status: 403 }
    );
  }

  const targetUrl = `${API_BASE}/${path.join("/")}${request.nextUrl.search}`;
  const headers = new Headers();
  for (const header of ["content-type", "cookie", "x-xsrf-token", "accept"]) {
    const value = request.headers.get(header);
    if (value) {
      headers.set(header, value);
    }
  }
  headers.set("host", new URL(API_BASE).host);
  const body = request.method === "GET" || request.method === "HEAD" ? null : await request.arrayBuffer();

  const response = await fetch(targetUrl, {
    method: request.method,
    headers,
    body,
    redirect: "manual"
  });

  return new Response(response.body, {
    status: response.status,
    headers: response.headers
  });
}

export { proxy as GET, proxy as POST, proxy as PUT, proxy as PATCH, proxy as DELETE, proxy as OPTIONS };
