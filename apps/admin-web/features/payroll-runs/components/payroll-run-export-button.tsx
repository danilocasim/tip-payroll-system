"use client";

import { FileDown } from "lucide-react";

import { Button } from "@payroll/ui";

export function PayrollRunExportButton() {
  return (
    <Button className="print:hidden" onClick={() => window.print()} type="button" variant="secondary">
      <FileDown className="h-4 w-4" />
      Export PDF
    </Button>
  );
}
