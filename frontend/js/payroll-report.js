checkRole();
renderNav();
if (!isManager()) {
  document.querySelectorAll('.manager-only').forEach(el => { el.style.display = 'none'; });
}

const filter = document.getElementById('campusFilter');
const body = document.getElementById('reportBody');

async function loadReport() {
  const campus = filter.value;
  const url = campus ? `${API_BASE}/payroll/report?campus=${encodeURIComponent(campus)}` : `${API_BASE}/payroll/report`;
  const res = await fetch(url);
  const data = await res.json();

  body.innerHTML = (data.rows || []).map(r => `
    <tr>
      <td>${r.name ?? ''}</td><td>${r.campus ?? ''}</td><td>${r.position ?? ''}</td><td>${r.workArea ?? ''}</td>
      <td>${Number(r.hoursWorked || 0).toFixed(2)}</td><td>${money(r.salary)}</td><td>${money(r.bonus)}</td>
      <td>${money(r.deductions)}</td><td>${money(r.netPay)}</td><td>${r.payPeriod ?? ''}</td>
    </tr>
  `).join('');

  const t = data.totals || {};
  document.getElementById('tSalary').textContent = money(t.totalSalary);
  document.getElementById('tBonus').textContent = money(t.totalBonus);
  document.getElementById('tDed').textContent = money(t.totalDeductions);
  document.getElementById('tNet').textContent = money(t.totalNetPay);
}

function exportCSV() {
  const rows = [...document.querySelectorAll('table tr')].map(tr =>
    [...tr.children].map(td => `"${td.innerText.replaceAll('"', '""')}"`).join(',')
  ).join('\n');
  const blob = new Blob([rows], { type: 'text/csv;charset=utf-8;' });
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'payroll-report.csv';
  a.click();
}

filter.addEventListener('change', loadReport);
document.getElementById('csvBtn').addEventListener('click', exportCSV);
document.getElementById('pdfBtn').addEventListener('click', () => window.print());
document.getElementById('saveBtn').addEventListener('click', async () => {
  const campus = filter.value;
  const url = campus ? `${API_BASE}/payroll/save?campus=${encodeURIComponent(campus)}` : `${API_BASE}/payroll/save`;
  const res = await fetch(url, { method: 'POST' });
  const data = await res.json();
  alert(`Saved ${data.savedRecords || 0} payroll records.`);
});

loadReport();
