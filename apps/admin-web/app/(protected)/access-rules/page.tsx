import { ShieldCheck, UserRoundCheck, UserRoundX } from "lucide-react";

import { Badge, Card, CardContent, CardDescription, CardHeader, CardTitle, PageHeader } from "@payroll/ui";

const rules = [
  {
    title: "Managers use the admin portal",
    description: "Only manager accounts can open admin routes and call manager-only APIs.",
    icon: ShieldCheck,
    badge: "MANAGER"
  },
  {
    title: "Employees do not self-sign up",
    description: "Employee access is provisioned by HR or payroll through invite-based onboarding.",
    icon: UserRoundCheck,
    badge: "INVITE ONLY"
  },
  {
    title: "Employees cannot use manager access",
    description: "The backend blocks employee accounts from admin resources even if a user attempts direct navigation.",
    icon: UserRoundX,
    badge: "ENFORCED"
  }
];

export default function AccessRulesPage() {
  return (
    <div className="space-y-8">
      <PageHeader
        title="Access rules"
        description="These are the live access boundaries for the payroll system. They are enforced by the backend, not only by frontend navigation."
      />

      <div className="grid gap-5 md:grid-cols-3">
        {rules.map(({ title, description, icon: Icon, badge }) => (
          <Card key={title}>
            <CardHeader>
              <div className="flex h-12 w-12 items-center justify-center rounded-[16px] bg-[var(--color-surface-tint)] text-[var(--color-ink-900)]">
                <Icon className="h-5 w-5" />
              </div>
              <CardTitle>{title}</CardTitle>
              <CardDescription>{description}</CardDescription>
            </CardHeader>
            <CardContent>
              <Badge>{badge}</Badge>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
