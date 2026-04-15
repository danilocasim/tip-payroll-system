import { Card, CardContent } from "@payroll/ui";

function currency(value: number): string {
  return new Intl.NumberFormat("en-PH", { style: "currency", currency: "PHP" }).format(value);
}

export function PayrollPreviewCard({
  hourlyRate,
  hoursWorked,
  bonus,
  deductions
}: {
  hourlyRate: number;
  hoursWorked: number;
  bonus: number;
  deductions: number;
}) {
  const grossPay = hourlyRate * hoursWorked;
  const netPay = grossPay + bonus - deductions;

  return (
    <Card className="bg-[linear-gradient(135deg,rgba(255,247,219,1),rgba(244,197,66,0.20))]">
      <CardContent className="grid gap-4 p-5 md:grid-cols-2 xl:grid-cols-4">
        <Metric label="Gross Pay" value={currency(grossPay)} />
        <Metric label="Bonus" value={currency(bonus)} />
        <Metric label="Deductions" value={currency(deductions)} />
        <Metric label="Projected Net Pay" value={currency(netPay)} strong />
      </CardContent>
    </Card>
  );
}

function Metric({ label, value, strong = false }: { label: string; value: string; strong?: boolean }) {
  return (
    <div>
      <p className="text-xs uppercase tracking-[0.08em] text-[var(--color-ink-500)]">{label}</p>
      <p className={strong ? "mt-2 text-xl font-semibold text-[var(--color-ink-900)]" : "mt-2 text-lg font-medium text-[var(--color-ink-900)]"}>{value}</p>
    </div>
  );
}
