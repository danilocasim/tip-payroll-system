import * as React from "react";

import { cn } from "./lib/cn";

export function PageHeader({
  title,
  description,
  action,
  className
}: {
  title: string;
  description?: string;
  action?: React.ReactNode;
  className?: string;
}) {
  return (
    <div className={cn("flex flex-col gap-4 md:flex-row md:items-end md:justify-between", className)}>
      <div className="space-y-2">
        <h1 className="text-3xl font-semibold tracking-[-0.03em] text-[var(--color-ink-900)]">{title}</h1>
        {description ? <p className="max-w-2xl text-sm leading-6 text-[var(--color-ink-500)]">{description}</p> : null}
      </div>
      {action ? <div className="shrink-0">{action}</div> : null}
    </div>
  );
}
