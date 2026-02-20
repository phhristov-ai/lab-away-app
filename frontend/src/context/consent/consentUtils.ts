import { ConsentState } from './types';

declare global {
  interface Window {
    dataLayer: any[];
    gtag?: (...args: any[]) => void;
  }
  var gtag: ((...args: any[]) => void) | undefined;
}

export function getStoredConsent(): ConsentState {
  const stored = localStorage.getItem("cookieConsent");
  if (!stored) return { hasInteracted: false };

  try {
    const parsed = JSON.parse(stored);
    return {
      hasInteracted: typeof parsed.hasInteracted === "boolean" ? parsed.hasInteracted : false,
    };
  } catch {
    return { hasInteracted: false };
  }
}


export function saveConsent(consent: ConsentState) {
  localStorage.setItem("cookieConsent", JSON.stringify(consent));
}


export function sendGAEvent(
  event: string,
  params: Record<string, unknown> = {}
) {
  if (typeof globalThis.gtag === 'function') {
    globalThis.gtag('event', event, params);
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
