import * as React from "react";

import { cn } from "./lib/cn";

type AlertTone = "default" | "error" | "success";

const toneClasses: Record<AlertTone, string> = {
  default: "border-[var(--color-line)] bg-[var(--color-surface-tint)] text-[var(--color-ink-800)]",
  error: "border-[#efc6c6] bg-[#fdf1f1] text-[var(--color-ink-900)]",
  success: "border-[#bfe3d8] bg-[#f0faf7] text-[var(--color-ink-900)]"
};

export function Alert({ className, tone = "default", ...props }: React.HTMLAttributes<HTMLDivElement> & { tone?: AlertTone }) {
  return <div role="alert" className={cn("rounded-[16px] border px-4 py-3 text-sm", toneClasses[tone], className)} {...props} />;
}
