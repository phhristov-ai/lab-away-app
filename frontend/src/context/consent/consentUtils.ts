import { ConsentState } from './types';

declare global {
  interface Window {
    dataLayer: any[];
    gtag?: (...args: any[]) => void;
  }
}

export function getStoredConsent(): ConsentState | null {
  const stored = localStorage.getItem('cookieConsent');
  if (!stored) return null;

  try {
    const parsed = JSON.parse(stored);
    if (parsed && typeof parsed === 'object' && 'necessary' in parsed) {
      return parsed;
    }
  } catch (error) {
    console.warn('Invalid cookieConsent in localStorage:', error);
  }
  return null;
}

export function saveConsent(consent: ConsentState) {
  localStorage.setItem('cookieConsent', JSON.stringify(consent));
}

export function ensureGoogleAnalytics() {
  if (typeof window.gtag !== 'function') {
    console.warn('[GA] gtag not found. Is the GA script in index.html?');
  } else {
    console.log('[GA] Ready');
  }
}

export function sendGAEvent(event: string, params: Record<string, any> = {}) {
  if (typeof window.gtag === 'function') {
    window.gtag('event', event, params);
    console.log('[GA Event]', event, params);
  } else {
    console.warn('[GA] Tried to send event before GA loaded:', event);
  }
}

export function loadGoogleAds() {
  if (document.getElementById('ads-script')) return;
  const script = document.createElement('script');
  script.async = true;
  script.src = 'https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js';
  script.id = 'ads-script';
  document.head.appendChild(script);
}

export function removeGoogleAds() {
  const script = document.getElementById('ads-script');
  if (script) script.remove();
  eraseCookie('IDE');
  eraseCookie('ANID');
  eraseCookie('DSID');
}

export function eraseCookie(name: string) {
  document.cookie = name + '=; Max-Age=-99999999;';
}
