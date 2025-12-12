const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');

const app = express();

// Serve React build
app.use(express.static(path.join(__dirname, 'build')));

// Proxy API requests to your Spring backend
app.use(
  '/api',
  createProxyMiddleware({
    target: 'https://api.lab-away.com',
    changeOrigin: true,
    secure: true,
  })
);

// Fallback for SPA routing
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'build', 'index.html'));
});

const PORT = 45679;
app.listen(PORT, () => console.log(`Prerender server running on http://localhost:${PORT}`));
