checkRole();
renderNav();
if (!isManager()) {
  document.querySelectorAll('.manager-only').forEach(el => { el.style.display = 'none'; });
}

const tbody = document.getElementById('employeesBody');
const filter = document.getElementById('campusFilter');

function netPay(e) {
  return Number(e.salary || 0) + Number(e.bonus || 0) - Number(e.deductions || 0);
}

async function loadEmployees() {
  const campus = filter.value;
  const url = campus ? `${API_BASE}/employees?campus=${encodeURIComponent(campus)}` : `${API_BASE}/employees`;
  const res = await fetch(url);
  const employees = await res.json();
  tbody.innerHTML = employees.map(e => `
    <tr>
      <td>${e.name ?? ''}</td>
      <td>${e.campus ?? ''}</td>
      <td>${e.position ?? ''}</td>
      <td>${e.workArea ?? ''}</td>
      <td>${money(e.salary)}</td>
      <td>${money(e.bonus)}</td>
      <td>${money(e.deductions)}</td>
      <td>${money(netPay(e))}</td>
      <td>
        <div class="actions">
          <a class="btn btn-outline manager-only" href="edit-employee.html?id=${e.employeeId}">Edit</a>
          <button class="btn btn-danger manager-only" data-id="${e.employeeId}">Delete</button>
        </div>
      </td>
    </tr>
  `).join('');

  if (!isManager()) {
    document.querySelectorAll('.manager-only').forEach(el => { el.style.display = 'none'; });
  }

  document.querySelectorAll('button[data-id]').forEach(btn => {
    btn.addEventListener('click', async () => {
      if (!confirm('Are you sure you want to delete this employee?')) return;
      await fetch(`${API_BASE}/employees/${btn.dataset.id}`, { method: 'DELETE' });
      loadEmployees();
    });
  });
}

filter.addEventListener('change', loadEmployees);
loadEmployees();
