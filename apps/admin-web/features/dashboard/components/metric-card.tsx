import type { LucideIcon } from "lucide-react";

import { Card, CardContent } from "@payroll/ui";

export function MetricCard({
  icon: Icon,
  label,
  value,
  accent
}: {
  icon: LucideIcon;
  label: string;
  value: string;
  accent: "soft" | "strong" | "neutral";
}) {
  const accentClass = {
    soft: "bg-[var(--color-surface-tint)]",
    strong: "bg-[linear-gradient(135deg,rgba(255,247,219,1),rgba(244,197,66,0.55))]",
    neutral: "bg-white"
  }[accent];

  return (
    <Card className={accentClass}>
      <CardContent className="flex items-start justify-between p-6">
        <div className="space-y-3">
          <p className="text-sm font-medium text-[var(--color-ink-500)]">{label}</p>
          <p className="text-3xl font-semibold tracking-[-0.04em] text-[var(--color-ink-900)]">{value}</p>
        </div>
        <div className="flex h-11 w-11 items-center justify-center rounded-[16px] border border-[var(--color-line)] bg-white/80 text-[var(--color-ink-900)]">
          <Icon className="h-5 w-5" />
        </div>
      </CardContent>
    </Card>
  );
}
