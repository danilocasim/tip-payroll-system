checkRole();
if (!isManager()) window.location.href = 'employees.html';
renderNav();

const form = document.getElementById('employeeForm');
const salaryPreview = document.getElementById('salaryPreview');
const netPreview = document.getElementById('netPreview');
const err = document.getElementById('formError');

const g = (id) => document.getElementById(id);
const n = (id) => Number(g(id).value || 0);

function recompute() {
  const salary = n('hourlyRate') * n('hoursWorked');
  const net = salary + n('bonus') - n('deductions');
  salaryPreview.textContent = salary.toFixed(2);
  netPreview.textContent = net.toFixed(2);
}

['hourlyRate', 'hoursWorked', 'bonus', 'deductions'].forEach(id => g(id).addEventListener('input', recompute));

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  err.textContent = '';
  if (!g('name').value.trim()) {
    err.textContent = 'Name is required.';
    return;
  }
  const payload = {
    name: g('name').value.trim(),
    position: g('position').value.trim(),
    workArea: g('workArea').value,
    campus: g('campus').value,
    hourlyRate: n('hourlyRate'),
    hoursWorked: n('hoursWorked'),
    bonus: n('bonus'),
    deductions: n('deductions'),
    payPeriod: g('payPeriod').value
  };

  const btn = form.querySelector('button[type="submit"]');
  if (btn) btn.disabled = true;

  try {
    const res = await fetch(`${API_BASE}/employees`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      err.textContent = await readApiError(res);
      return;
    }
    window.location.href = 'employees.html';
  } catch (ex) {
    err.textContent =
      'Cannot reach the server. Start the backend with: mvn spring-boot:run (from payroll-system/backend), ' +
      'then ensure this page can call http://localhost:8080. Browser error: ' +
      (ex && ex.message ? ex.message : String(ex));
  } finally {
    if (btn) btn.disabled = false;
  }
});

recompute();
