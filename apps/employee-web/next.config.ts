import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  transpilePackages: ["@payroll/ui", "@payroll/api-client", "@payroll/auth", "@payroll/types"]
};

export default nextConfig;
