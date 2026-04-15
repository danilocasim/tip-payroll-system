import { CAMPUS_OPTIONS } from "@payroll/types";
import { Button, Card, CardContent, Input, Select } from "@payroll/ui";

export function EmployeesFilters({ currentSearch, currentCampus }: { currentSearch: string; currentCampus: string }) {
  return (
    <Card>
      <CardContent className="p-5">
        <form className="grid gap-4 md:grid-cols-[1fr_220px_auto]" method="GET">
          <label className="space-y-2 text-sm font-medium text-[var(--color-ink-700)]">
            <span>Search employees</span>
            <Input defaultValue={currentSearch} name="search" placeholder="Name, position, campus, or work area" />
          </label>

          <label className="space-y-2 text-sm font-medium text-[var(--color-ink-700)]">
            <span>Campus</span>
            <Select defaultValue={currentCampus} name="campus">
              <option value="">All campuses</option>
              {CAMPUS_OPTIONS.map((campus) => (
                <option key={campus} value={campus}>
                  {campus}
                </option>
              ))}
            </Select>
          </label>

          <div className="flex items-end gap-3">
            <Button type="submit">Apply filters</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
