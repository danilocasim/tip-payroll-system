import type { Metadata } from "next";

import "./globals.css";

export const metadata: Metadata = {
  title: "Payroll Employee Portal",
  description: "Self-service payroll access for employees"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
