import { EmployeeInviteActivationForm } from "@/features/auth/components/employee-invite-activation-form";

export default async function EmployeeActivatePage({ params }: { params: Promise<{ token: string }> }) {
  const { token } = await params;

  return (
    <main className="min-h-screen px-6 py-8 md:px-10 lg:px-12">
      <div className="mx-auto grid min-h-[calc(100vh-4rem)] max-w-6xl items-center gap-8 lg:grid-cols-[0.9fr_1.1fr]">
        <section>
          <div className="max-w-xl space-y-6">
            <p className="text-sm font-medium uppercase tracking-[0.18em] text-[var(--color-ink-500)]">Secure employee onboarding</p>
            <h1 className="text-4xl font-semibold tracking-[-0.04em] text-[var(--color-ink-900)] md:text-5xl">
              Activate your employee portal access.
            </h1>
            <p className="text-base leading-7 text-[var(--color-ink-500)]">
              This one-time link lets you create your password and start using the employee portal without exposing public signup.
            </p>
          </div>
        </section>
        <section>
          <EmployeeInviteActivationForm token={token} />
        </section>
      </div>
    </main>
  );
}
