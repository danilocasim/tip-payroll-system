import * as React from "react";

import { cn } from "./lib/cn";

type ButtonVariant = "primary" | "secondary" | "ghost" | "danger";
type ButtonSize = "sm" | "md" | "lg";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  asChild?: boolean;
  variant?: ButtonVariant;
  size?: ButtonSize;
}

const variantClasses: Record<ButtonVariant, string> = {
  primary: "bg-[var(--color-primary)] text-[var(--color-ink-900)] hover:bg-[var(--color-primary-strong)] shadow-[var(--shadow-sm)]",
  secondary: "border border-[var(--color-line-strong)] bg-white text-[var(--color-ink-900)] hover:bg-[var(--color-surface-tint)]",
  ghost: "bg-transparent text-[var(--color-ink-700)] hover:bg-[var(--color-surface-tint)]",
  danger: "bg-[var(--color-error)] text-white hover:opacity-90"
};

const sizeClasses: Record<ButtonSize, string> = {
  sm: "h-10 px-4 text-sm",
  md: "h-11 px-5 text-sm",
  lg: "h-12 px-6 text-base"
};

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { asChild = false, className, variant = "primary", size = "md", children, ...props },
  ref
) {
  const mergedClassName = cn(
    "inline-flex items-center justify-center gap-2 rounded-[12px] font-medium transition duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-primary-strong)] focus-visible:ring-offset-2 focus-visible:ring-offset-[var(--color-canvas)] disabled:pointer-events-none disabled:opacity-50",
    variantClasses[variant],
    sizeClasses[size],
    className
  );

  if (asChild && React.isValidElement(children)) {
    const child = children as React.ReactElement<{ className?: string }>;
    return React.cloneElement(child, {
      className: cn(mergedClassName, child.props.className)
    });
  }

  return (
    <button
      ref={ref}
      className={mergedClassName}
      {...props}
    >
      {children}
    </button>
  );
});
