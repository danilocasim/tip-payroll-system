document.getElementById('enterManager').addEventListener('click', () => {
  sessionStorage.setItem('role', 'manager');
  window.location.href = 'dashboard.html';
});

document.getElementById('enterEmployee').addEventListener('click', () => {
  sessionStorage.setItem('role', 'employee');
  window.location.href = 'dashboard.html';
});
