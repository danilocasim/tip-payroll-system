"use client";

import { FileDown } from "lucide-react";

import { Button } from "@payroll/ui";

export function ExportPdfButton() {
  function handleExport() {
    window.print();
  }

  return (
    <Button className="print:hidden" onClick={handleExport} type="button" variant="secondary">
      <FileDown className="h-4 w-4" />
      Export PDF
    </Button>
  );
}
