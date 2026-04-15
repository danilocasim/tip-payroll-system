import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{ts,tsx}",
    "./features/**/*.{ts,tsx}",
    "./lib/**/*.{ts,tsx}",
    "../../packages/ui/src/**/*.{ts,tsx}",
    "../../packages/api-client/src/**/*.ts",
    "../../packages/auth/src/**/*.ts",
    "../../packages/types/src/**/*.ts"
  ],
  theme: {
    extend: {}
  },
  plugins: []
};

export default config;
