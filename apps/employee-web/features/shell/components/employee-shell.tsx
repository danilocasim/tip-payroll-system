import Link from "next/link";
import { CreditCard, LayoutDashboard, UserRound } from "lucide-react";

import type { AuthUser } from "@payroll/types";
import { Badge } from "@payroll/ui";
import { LogoutButton } from "@/features/auth/components/logout-button";

const navItems = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/profile", label: "Profile", icon: UserRound },
  { href: "/payroll-history", label: "Payroll History", icon: CreditCard }
];

export function EmployeeShell({ user, children }: { user: AuthUser; children: React.ReactNode }) {
  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-30 border-b border-[var(--color-line)] bg-[rgba(255,253,248,0.85)] backdrop-blur-xl">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between gap-4 px-6 py-4 md:px-8">
          <div>
            <p className="text-sm font-medium text-[var(--color-ink-500)]">Employee Portal</p>
            <p className="text-lg font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">Hello, {user.email}</p>
          </div>
          <div className="flex items-center gap-3">
            <Badge>{user.role}</Badge>
            <LogoutButton />
          </div>
        </div>
        <nav className="mx-auto flex w-full max-w-6xl gap-2 overflow-x-auto px-6 pb-4 md:px-8">
          {navItems.map(({ href, label, icon: Icon }) => (
            <Link
              key={href}
              href={href}
              className="inline-flex items-center gap-2 rounded-full border border-[var(--color-line)] bg-white/70 px-4 py-2 text-sm font-medium text-[var(--color-ink-700)] transition hover:bg-[var(--color-surface-tint)]"
            >
              <Icon className="h-4 w-4" />
              {label}
            </Link>
          ))}
        </nav>
      </header>
      <main className="mx-auto flex w-full max-w-6xl flex-col px-6 py-8 md:px-8">{children}</main>
    </div>
  );
}
