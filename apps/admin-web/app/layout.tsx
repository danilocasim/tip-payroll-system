import type { Metadata } from "next";

import "./globals.css";

export const metadata: Metadata = {
  title: "Payroll Admin Portal",
  description: "Manager operations portal for the TIP payroll system"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
