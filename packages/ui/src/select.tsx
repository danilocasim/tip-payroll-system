import * as React from "react";

import { cn } from "./lib/cn";

export const Select = React.forwardRef<HTMLSelectElement, React.SelectHTMLAttributes<HTMLSelectElement>>(function Select(
  { className, children, ...props },
  ref
) {
  return (
    <select
      ref={ref}
      className={cn(
        "flex h-11 w-full rounded-[12px] border border-[var(--color-line-strong)] bg-white px-4 text-sm text-[var(--color-ink-900)] outline-none transition focus:border-[var(--color-primary-strong)] focus:ring-2 focus:ring-[var(--color-primary-soft)] disabled:cursor-not-allowed disabled:opacity-60",
        className
      )}
      {...props}
    >
      {children}
    </select>
  );
});
