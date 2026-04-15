checkRole();
renderNav();
if (!isManager()) {
  document.querySelectorAll('.manager-only').forEach(el => { el.style.display = 'none'; });
}

async function loadSummary() {
  const res = await fetch(`${API_BASE}/dashboard/summary`);
  const data = await res.json();
  document.getElementById('totalEmployees').textContent = data.totalEmployees ?? 0;
  document.getElementById('totalPayroll').textContent = money(data.totalPayroll);
  document.getElementById('avgNetPay').textContent = money(data.avgNetPay);
}

loadSummary();
