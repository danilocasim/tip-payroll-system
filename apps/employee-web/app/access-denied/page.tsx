import Link from "next/link";
import { Lock } from "lucide-react";

import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle } from "@payroll/ui";

export default function AccessDeniedPage() {
  return (
    <main className="flex min-h-screen items-center justify-center px-6 py-12">
      <Card className="max-w-lg">
        <CardHeader>
          <div className="flex h-14 w-14 items-center justify-center rounded-[18px] bg-[var(--color-surface-tint)] text-[var(--color-ink-900)]">
            <Lock className="h-7 w-7" />
          </div>
          <CardTitle>Portal access is restricted</CardTitle>
          <CardDescription>
            This page is available only to the correct authenticated employee portal session. Return to your login screen to continue.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Button asChild>
            <Link href="/login">Return to employee login</Link>
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
