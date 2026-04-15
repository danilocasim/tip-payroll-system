import { requireEmployeeUser } from "@/lib/server/backend";
import { EmployeeShell } from "@/features/shell/components/employee-shell";

export default async function ProtectedLayout({ children }: { children: React.ReactNode }) {
  const user = await requireEmployeeUser();

  return <EmployeeShell user={user}>{children}</EmployeeShell>;
}
