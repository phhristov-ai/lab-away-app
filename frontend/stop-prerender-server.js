const { exec } = require('child_process');

const PORT = 45679;

if (process.platform === 'win32') {
  exec(`for /f "tokens=5" %a in ('netstat -aon ^| find ":${PORT}" ^| find "LISTENING"') do taskkill /PID %a /F`);
} else {
  exec(`kill $(lsof -t -i:${PORT})`);
}

console.log("🛑 Stopping prerender server...");
