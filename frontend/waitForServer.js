const net = require('net');

const PORT = 45679;

function waitForPort(port, retry = 0) {
  const socket = new net.Socket();

  socket.once('connect', () => {
    console.log(`✅ Prerender server detected on port ${port}`);
    socket.destroy();
    process.exit(0);
  });

  socket.once('error', () => {
    if (retry > 50) {
      console.error("❌ Server did not start.");
      process.exit(1);
    }
    setTimeout(() => waitForPort(port, retry + 1), 200);
  });

  socket.connect(port, '127.0.0.1');
}

waitForPort(PORT);
