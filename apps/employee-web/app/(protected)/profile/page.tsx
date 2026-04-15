import { Badge, Card, CardContent, CardDescription, CardHeader, CardTitle, PageHeader } from "@payroll/ui";

import { getEmployeeProfile } from "@/lib/server/backend";

export default async function EmployeeProfilePage() {
  const profile = await getEmployeeProfile();

  return (
    <div className="space-y-8">
      <PageHeader
        title="Your profile"
        description="These details are shown from your protected employee record. If something looks incorrect, contact your payroll administrator."
      />

      <div className="grid gap-5 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>{profile.fullName}</CardTitle>
            <CardDescription>Employee number: {profile.employeeNumber}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4 text-sm text-[var(--color-ink-700)]">
            <div className="flex items-center justify-between gap-4 border-t border-[var(--color-line)] pt-4">
              <span>Campus</span>
              <Badge>{profile.campus ?? "Unassigned"}</Badge>
            </div>
            <div className="flex items-center justify-between gap-4 border-t border-[var(--color-line)] pt-4">
              <span>Position</span>
              <span className="font-medium text-[var(--color-ink-900)]">{profile.position ?? "-"}</span>
            </div>
            <div className="flex items-center justify-between gap-4 border-t border-[var(--color-line)] pt-4">
              <span>Work Area</span>
              <span className="font-medium text-[var(--color-ink-900)]">{profile.workArea ?? "-"}</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Payroll baseline</CardTitle>
            <CardDescription>Read-only information for the first release of the employee portal.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4 text-sm text-[var(--color-ink-700)]">
            <div className="flex items-center justify-between gap-4 border-t border-[var(--color-line)] pt-4">
              <span>Hourly Rate</span>
              <span className="font-medium tabular-nums text-[var(--color-ink-900)]">
                {new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(Number(profile.hourlyRate))}
              </span>
            </div>
            <div className="flex items-center justify-between gap-4 border-t border-[var(--color-line)] pt-4">
              <span>Status</span>
              <Badge>{profile.employmentStatus}</Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
