import { AdminLoginForm } from "@/features/auth/components/admin-login-form";

export default function AdminLoginPage() {
  return (
    <main className="min-h-screen px-6 py-8 md:px-10 lg:px-12">
      <div className="mx-auto grid min-h-[calc(100vh-4rem)] max-w-7xl items-center gap-8 lg:grid-cols-[1.05fr_0.95fr]">
        <section className="order-2 lg:order-1">
          <div className="max-w-xl space-y-6">
            <p className="text-sm font-medium uppercase tracking-[0.18em] text-[var(--color-ink-500)]">TIP Payroll</p>
            <div className="space-y-4">
              <h1 className="max-w-lg text-4xl font-semibold tracking-[-0.04em] text-[var(--color-ink-900)] md:text-5xl">
                Quiet, secure control over payroll operations.
              </h1>
              <p className="max-w-lg text-base leading-7 text-[var(--color-ink-500)]">
                The manager portal is designed for clean review, fast employee management, and reliable payroll oversight without visual noise.
              </p>
            </div>
            <div className="grid gap-4 text-sm text-[var(--color-ink-700)] md:grid-cols-2">
              <div className="rounded-[20px] border border-[var(--color-line)] bg-white/75 p-5 backdrop-blur-sm">
                <div className="mb-2 h-2 w-16 rounded-full bg-[var(--color-primary)]" />
                Protected manager-only routes, audited login events, and server-trusted access control.
              </div>
              <div className="rounded-[20px] border border-[var(--color-line)] bg-white/75 p-5 backdrop-blur-sm">
                <div className="mb-2 h-2 w-16 rounded-full bg-[var(--color-primary-soft)]" />
                Warm, minimal visuals tuned for data-heavy tasks without enterprise clutter.
              </div>
            </div>
          </div>
        </section>
        <section className="order-1 lg:order-2">
          <AdminLoginForm />
        </section>
      </div>
    </main>
  );
}
