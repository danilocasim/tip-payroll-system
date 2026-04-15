import type { LucideIcon } from "lucide-react";

import { Card, CardContent } from "@payroll/ui";

export function SummaryCard({
  icon: Icon,
  label,
  value,
  featured = false
}: {
  icon: LucideIcon;
  label: string;
  value: string;
  featured?: boolean;
}) {
  return (
    <Card className={featured ? "bg-[linear-gradient(135deg,rgba(255,248,227,1),rgba(244,197,66,0.25))]" : "bg-white"}>
      <CardContent className="flex items-start justify-between p-6">
        <div className="space-y-3">
          <p className="text-sm font-medium text-[var(--color-ink-500)]">{label}</p>
          <p className="text-2xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">{value}</p>
        </div>
        <div className="flex h-11 w-11 items-center justify-center rounded-[16px] border border-[var(--color-line)] bg-white/80 text-[var(--color-ink-900)]">
          <Icon className="h-5 w-5" />
        </div>
      </CardContent>
    </Card>
  );
}
