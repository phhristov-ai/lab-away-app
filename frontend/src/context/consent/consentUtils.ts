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
    if (typeof parsed === 'object' && parsed !== null && 'necessary' in parsed) {
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

export function loadGoogleAnalytics() {
  if (document.getElementById('ga-script')) return;

  const script = document.createElement('script');
  script.async = true;
  script.src = 'https://www.googletagmanager.com/gtag/js?id=G-XXXXXXXXXX';
  script.id = 'ga-script';
  document.head.appendChild(script);

  window.dataLayer = window.dataLayer || [];
  function gtag(...args: any[]) {
    window.dataLayer.push(args);
  }
  (window as any).gtag = gtag;
  gtag('js', new Date());
  gtag('config', 'G-XXXXXXXXXX');
}

export function removeGoogleAnalytics() {
  const script = document.getElementById('ga-script');
  if (script) script.remove();
  eraseCookie('_ga');
  eraseCookie('_gid');
  eraseCookie('_gat');
  (window as any).gtag = () => {};
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
