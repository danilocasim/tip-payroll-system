import * as React from "react";

import { cn } from "./lib/cn";

export function Badge({ className, ...props }: React.HTMLAttributes<HTMLSpanElement>) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full border border-[var(--color-line)] bg-[var(--color-surface-tint)] px-3 py-1 text-xs font-medium text-[var(--color-ink-700)]",
        className
      )}
      {...props}
    />
  );
}
