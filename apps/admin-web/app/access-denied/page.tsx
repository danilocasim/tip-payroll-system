import Link from "next/link";
import { ShieldX } from "lucide-react";

import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle } from "@payroll/ui";

export default function AccessDeniedPage() {
  return (
    <main className="flex min-h-screen items-center justify-center px-6 py-12">
      <Card className="max-w-lg">
        <CardHeader>
          <div className="flex h-14 w-14 items-center justify-center rounded-[18px] bg-[var(--color-surface-tint)] text-[var(--color-ink-900)]">
            <ShieldX className="h-7 w-7" />
          </div>
          <CardTitle>Access denied</CardTitle>
          <CardDescription>
            This admin area is restricted to authorized manager accounts. If you think this is a mistake, contact your system administrator.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex gap-3">
          <Button asChild variant="secondary">
            <Link href="/login">Return to login</Link>
          </Button>
          <Button asChild>
            <Link href="/dashboard">Go to dashboard</Link>
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
