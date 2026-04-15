export interface ApiError {
  code: string;
  message: string;
  fields?: Record<string, string>;
}

export interface ApiResponse<T> {
  ok: boolean;
  data?: T;
  error?: ApiError;
}

export type PortalRole = "MANAGER" | "EMPLOYEE";

export const CAMPUS_OPTIONS = ["Casal", "Arlegui"] as const;
export type CampusOption = (typeof CAMPUS_OPTIONS)[number];

export interface AuthUser {
  id: string;
  email: string;
  role: PortalRole;
  employeeProfileId: string | null;
}

export interface AuthMeData {
  user: AuthUser;
  portalAccess: string[];
}

export interface AdminDashboardSummary {
  totalEmployees: number;
  totalPayroll: string;
  avgNetPay: string;
}

export interface AdminEmployeeListItem {
  employeeId: number;
  name: string;
  campus: string | null;
  position: string | null;
  workArea: string | null;
  hourlyRate: string;
  hoursWorked: string;
  salary: string;
  bonus: string;
  deductions: string;
  netPay: string;
  payPeriod: string | null;
}

export interface AdminEmployeeDetail {
  employeeId: number;
  name: string;
  firstName: string;
  lastName: string;
  campus: string;
  position: string | null;
  workArea: string | null;
  hourlyRate: string;
  hoursWorked: string;
  salary: string;
  bonus: string;
  deductions: string;
  netPay: string;
  payPeriod: string | null;
}

export interface AdminEmployeesPage {
  items: AdminEmployeeListItem[];
  page: number;
  perPage: number;
  total: number;
}

export interface AdminCreateEmployeeRequest {
  employeeNumber: string;
  firstName: string;
  lastName: string;
  campus: string;
  position: string;
  workArea: string;
  hourlyRate: string;
  hoursWorked: string;
  bonus: string;
  deductions: string;
  payPeriod: string;
  createPortalAccess: boolean;
  email?: string;
}

export interface ProvisionedEmployee {
  employeeId: number;
  employeeProfileId: string;
  employeeNumber: string;
  fullName: string;
  email: string | null;
  portalAccessCreated: boolean;
  inviteUrl: string | null;
  inviteExpiresAt: string | null;
}

export interface AdminUpdateEmployeeRequest {
  firstName: string;
  lastName: string;
  campus: string;
  position: string;
  workArea: string;
  hourlyRate: string;
  hoursWorked: string;
  bonus: string;
  deductions: string;
  payPeriod: string;
}

export interface CreatePayrollRunRequest {
  payPeriodStart: string;
  payPeriodEnd: string;
  campusScope?: string;
}

export interface PayrollRunListItem {
  id: string;
  payPeriodStart: string;
  payPeriodEnd: string;
  campusScope: string | null;
  status: "DRAFT" | "FINALIZED" | "CANCELLED";
  employeeCount: number;
  totalNetPay: string;
  createdAt: string;
  finalizedAt: string | null;
}

export interface PayrollRunRecord {
  recordId: number;
  employeeId: number | null;
  employeeName: string;
  campus: string | null;
  position: string | null;
  salary: string;
  bonus: string;
  deductions: string;
  netPay: string;
  payPeriod: string;
  generatedAt: string | null;
}

export interface PayrollRunDetail {
  id: string;
  payPeriodStart: string;
  payPeriodEnd: string;
  campusScope: string | null;
  status: "DRAFT" | "FINALIZED" | "CANCELLED";
  employeeCount: number;
  totalSalary: string;
  totalBonus: string;
  totalDeductions: string;
  totalNetPay: string;
  createdAt: string;
  finalizedAt: string | null;
  records: PayrollRunRecord[];
}

export interface EmployeeInviteView {
  employeeName: string;
  email: string;
  expiresAt: string;
}

export interface EmployeeDashboardSummary {
  employeeNumber: string;
  fullName: string;
  latestPayPeriod: string | null;
  latestNetPay: string;
  payrollRecordCount: number;
}

export interface EmployeeProfile {
  id: string;
  employeeNumber: string;
  fullName: string;
  campus: string | null;
  position: string | null;
  workArea: string | null;
  hourlyRate: string;
  employmentStatus: string;
}

export interface EmployeePayrollRecordSummary {
  recordId: number;
  salary: string;
  bonus: string;
  deductions: string;
  netPay: string;
  payPeriod: string | null;
  generatedAt: string | null;
}

export interface EmployeePayrollRecordsPage {
  items: EmployeePayrollRecordSummary[];
  page: number;
  perPage: number;
  total: number;
}

export interface EmployeePayrollRecordDetail {
  recordId: number;
  employeeName: string;
  salary: string;
  bonus: string;
  deductions: string;
  netPay: string;
  payPeriod: string | null;
  generatedAt: string | null;
}
