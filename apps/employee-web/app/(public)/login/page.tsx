import { EmployeeLoginForm } from "@/features/auth/components/employee-login-form";

export default function EmployeeLoginPage() {
  return (
    <main className="min-h-screen px-6 py-8 md:px-10 lg:px-12">
      <div className="mx-auto grid min-h-[calc(100vh-4rem)] max-w-6xl items-center gap-8 lg:grid-cols-[0.95fr_1.05fr]">
        <section>
          <div className="max-w-xl space-y-6">
            <p className="text-sm font-medium uppercase tracking-[0.18em] text-[var(--color-ink-500)]">Employee self-service</p>
            <div className="space-y-4">
              <h1 className="max-w-lg text-4xl font-semibold tracking-[-0.04em] text-[var(--color-ink-900)] md:text-5xl">
                Clear access to your payroll, without the clutter.
              </h1>
              <p className="max-w-xl text-base leading-7 text-[var(--color-ink-500)]">
                Review your latest pay period, confirm profile details, and track payroll history in a calmer, mobile-friendly portal built for everyday use.
              </p>
            </div>
            <div className="grid gap-4 text-sm text-[var(--color-ink-700)] md:grid-cols-2">
              <div className="rounded-[20px] border border-[var(--color-line)] bg-white/85 p-5 backdrop-blur-sm">
                Soft visual hierarchy keeps important payroll numbers easy to read.
              </div>
              <div className="rounded-[20px] border border-[var(--color-line)] bg-white/85 p-5 backdrop-blur-sm">
                Protected server-side authentication ensures your view is tied to your actual account.
              </div>
            </div>
          </div>
        </section>
        <section>
          <EmployeeLoginForm />
          <p className="mt-4 text-sm text-[var(--color-ink-500)]">
            First time here? Use the invite link shared by HR or payroll to activate your employee portal access.
          </p>
        </section>
      </div>
    </main>
  );
}
