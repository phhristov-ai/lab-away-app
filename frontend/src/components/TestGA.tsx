// TestGA.tsx
import React, { useEffect } from 'react';

const GA_TRACKING_ID = 'G-XY0MR2YPT8';

function initGA() {
  if (window.gtag) {
    console.log('[GA] already initialized');
    return;
  }

  window.dataLayer = window.dataLayer || [];
  function gtag(...args: any[]) {
    console.log('[GA call]', ...args);
    window.dataLayer.push(args);
  }
  window.gtag = gtag;

  const script = document.createElement('script');
  script.async = true;
  script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_TRACKING_ID}`;
  script.onload = () => {
    console.log('[GA] Script loaded successfully ✅');
    gtag('js', new Date());
    gtag('config', GA_TRACKING_ID, { send_page_view: true });
  };
  script.onerror = (err) => {
    console.error('[GA] Script failed to load ❌', err);
  };
  document.head.appendChild(script);
}

function sendEvent() {
  if (typeof window.gtag !== 'function') {
    console.warn('[GA] Not ready yet, retrying...');
    return;
  }

  console.log('[GA Event] test_event', { test_param: 'hello_world' });
  window.gtag('event', 'test_event', { test_param: 'hello_world' });
}

export default function TestGA() {
  useEffect(() => {
    initGA();
  }, []);

  return (
    <div style={{ padding: 40 }}>
      <h1>🧪 Google Analytics Test</h1>
      <p>Click the button below to send a test event.</p>
      <button onClick={sendEvent}>Send GA Event</button>
    </div>
  );
}
