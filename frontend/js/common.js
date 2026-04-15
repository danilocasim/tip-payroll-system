const API_BASE = 'http://localhost:8080/api';

function getRole() { return sessionStorage.getItem('role'); }
function isManager() { return getRole() === 'manager'; }
function checkRole() { if (!getRole()) window.location.href = 'landing.html'; }

function navTemplate() {
  const role = getRole() === 'manager' ? 'Manager' : 'Employee';
  const roleClass = getRole() === 'manager' ? 'manager' : 'employee';
  return `
    <header class="navbar"><div class="container nav-inner">
      <div class="brand">TIP Cafeteria Payroll</div>
      <nav class="nav-links">
        <a href="dashboard.html">Dashboard</a>
        <a href="employees.html">Employees</a>
        <a href="payroll-report.html">Payroll Report</a>
      </nav>
      <div class="top-right">
        <span class="badge ${roleClass}">${role}</span>
        <a class="switch-role" href="#" id="switchRole">Switch Role</a>
      </div>
    </div></header>`;
}

function renderNav() {
  const nav = document.getElementById('nav');
  if (!nav) return;
  nav.innerHTML = navTemplate();
  document.getElementById('switchRole').addEventListener('click', (e) => {
    e.preventDefault();
    sessionStorage.removeItem('role');
    window.location.href = 'landing.html';
  });
}

function money(v) {
  return `PHP ${Number(v || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

/** Parse Spring / fetch error body so the UI is never silent on failure. */
async function readApiError(response) {
  const text = await response.text();
  try {
    const j = JSON.parse(text);
    return j.error || j.message || j.detail || text || response.statusText;
  } catch {
    return text || response.statusText || `HTTP ${response.status}`;
  }
}
