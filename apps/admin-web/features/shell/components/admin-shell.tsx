import Link from "next/link";
import { BarChart3, LayoutDashboard, ReceiptText, Users } from "lucide-react";

import type { AuthUser } from "@payroll/types";
import { Badge } from "@payroll/ui";
import { LogoutButton } from "@/features/auth/components/logout-button";

const navItems = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/employees", label: "Employees", icon: Users },
  { href: "/payroll-runs", label: "Payroll Runs", icon: ReceiptText },
  { href: "/access-rules", label: "Access Rules", icon: BarChart3 }
];

export function AdminShell({ user, children }: { user: AuthUser; children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-transparent">
      <div className="grid min-h-screen lg:grid-cols-[290px_1fr]">
        <aside className="hidden border-r border-[var(--color-line)] bg-white/78 p-6 backdrop-blur-xl lg:block">
          <div className="flex h-full flex-col justify-between rounded-[24px] border border-[var(--color-line)] bg-white/80 p-5 shadow-[var(--shadow-md)]">
            <div className="space-y-8">
              <div className="space-y-2">
                <p className="text-sm font-medium uppercase tracking-[0.16em] text-[var(--color-ink-500)]">Manager portal</p>
                <h1 className="text-2xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">TIP Payroll</h1>
              </div>
              <nav className="space-y-2">
                {navItems.map(({ href, label, icon: Icon }) => (
                  <Link
                    key={href}
                    href={href}
                    className="flex items-center gap-3 rounded-[16px] px-4 py-3 text-sm font-medium text-[var(--color-ink-700)] transition hover:bg-[var(--color-surface-tint)]"
                  >
                    <Icon className="h-4 w-4" />
                    {label}
                  </Link>
                ))}
              </nav>
            </div>
            <div className="space-y-4 rounded-[18px] border border-[var(--color-line)] bg-[var(--color-surface-tint)]/70 p-4">
              <div>
                <p className="text-xs uppercase tracking-[0.14em] text-[var(--color-ink-500)]">Signed in</p>
                <p className="mt-1 text-sm font-medium text-[var(--color-ink-900)]">{user.email}</p>
              </div>
              <Badge>Manager access</Badge>
            </div>
          </div>
        </aside>

        <div className="flex min-h-screen flex-col">
          <header className="sticky top-0 z-30 border-b border-[var(--color-line)] bg-[rgba(255,252,245,0.82)] backdrop-blur-xl">
            <div className="mx-auto flex w-full max-w-7xl items-center justify-between gap-4 px-6 py-4 md:px-8">
              <div>
                <p className="text-sm font-medium text-[var(--color-ink-500)]">Protected operations</p>
                <p className="text-lg font-semibold tracking-[-0.02em] text-[var(--color-ink-900)]">Manager control center</p>
              </div>
              <div className="flex items-center gap-3">
                <Badge className="hidden md:inline-flex">{user.role}</Badge>
                <LogoutButton />
              </div>
            </div>
          </header>
          <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col px-6 py-8 md:px-8">{children}</main>
        </div>
      </div>
    </div>
  );
}
