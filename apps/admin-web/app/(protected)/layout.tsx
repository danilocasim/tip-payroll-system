import { requireAdminUser } from "@/lib/server/backend";
import { AdminShell } from "@/features/shell/components/admin-shell";

export default async function ProtectedLayout({ children }: { children: React.ReactNode }) {
  const user = await requireAdminUser();

  return <AdminShell user={user}>{children}</AdminShell>;
}
